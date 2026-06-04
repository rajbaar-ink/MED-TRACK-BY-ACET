-- =====================================================================
-- Supabase SQL Schema for MedTrack Application Database Configuration
-- Includes 'medicines' table, indexes, and Row-Level Security (RLS) rules.
-- This file handles both local and production-grade clinical asset security.
-- =====================================================================

-- 1. Enable UUID Extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Create the Medicines Table referencing Auth.Users schema
CREATE TABLE IF NOT EXISTS public.medicines (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL, -- e.g., "Daily", "Weekly", "As needed"
    times_string VARCHAR(255), -- e.g., "08:00,14:00,20:00"
    instructions TEXT, -- e.g., "Take after meals"
    qty_remaining INTEGER NOT NULL DEFAULT 30,
    qty_needed INTEGER NOT NULL DEFAULT 5, -- Low stock warning threshold
    is_notification_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    category_color_value BIGINT NOT NULL DEFAULT 4283115856, -- Color representation
    type VARCHAR(100) NOT NULL DEFAULT 'Pill', -- "Pill", "Syrup", "Injection", etc.
    start_date DATE NOT NULL DEFAULT CURRENT_DATE, -- Requested start date of the medication course
    end_date DATE, -- Requested end date of the medication course (null means ongoing/chronic)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 3. Enable Row-Level Security (RLS)
-- This protects medicines so users can only access their own clinical records
ALTER TABLE public.medicines ENABLE ROW LEVEL SECURITY;

-- 4. Set security access control policies
CREATE POLICY "Allow users to read their own medicines"
    ON public.medicines FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Allow users to insert their own medicines"
    ON public.medicines FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Allow users to update their own medicines"
    ON public.medicines FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Allow users to delete their own medicines"
    ON public.medicines FOR DELETE
    USING (auth.uid() = user_id);

-- 5. Additional Performance-enhancing Indexes
-- Speeds up queries running against specific patient credentials
CREATE INDEX IF NOT EXISTS idx_medicines_user_id ON public.medicines (user_id);
CREATE INDEX IF NOT EXISTS idx_medicines_start_end_date ON public.medicines (start_date, end_date);

-- 6. Trigger for Updating timestamps
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_medicines_modtime
    BEFORE UPDATE ON public.medicines
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_column();

-- =====================================================================
-- Supabase Authentication setup file (handles login/signup rules via PostgreSQL)
-- =====================================================================

-- Automatically create a profile or set up introductory data upon authentication sign up
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    full_name TEXT,
    updated_at TIMESTAMP WITH TIME ZONE
);

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view their own profile."
    ON public.profiles FOR SELECT
    USING (auth.uid() = id);

CREATE POLICY "Users can update their own profile."
    ON public.profiles FOR UPDATE
    USING (auth.uid() = id);

-- Handle automatic profile on Auth sign-up
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO public.profiles (id, full_name, updated_at)
  VALUES (new.id, new.raw_user_meta_data->>'full_name', NOW());
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();
