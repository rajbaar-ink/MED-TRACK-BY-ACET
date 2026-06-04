import { auth } from './firebaseConfig';

/**
 * fcmNotificationService (React / Web Native wrapper)
 * Coordinates token registration and local web push notification triggers on prescription reminders.
 */
export const registerForPushNotifications = async () => {
  try {
    if (!('Notification' in window)) {
      console.warn('This browser does not support local notification system alerts.');
      return null;
    }

    const permission = await Notification.requestPermission();
    if (permission !== 'granted') {
      console.warn('Notification permission denied by user.');
      return null;
    }

    // In production web, we request token using firebase/messaging:
    // const messaging = getMessaging();
    // val token = await getToken(messaging, { vapidKey: '...' });
    const mockToken = 'fcm_token_acet_medtrack_web_' + Math.random().toString(36).substring(7);
    console.log('[FCM Service] Securely configured push token:', mockToken);
    
    return mockToken;
  } catch (err) {
    console.error('[FCM Service] Error registering for push notifications:', err);
    return null;
  }
};

/**
 * Triggers a local browser/alert push notification for upcoming medication schedules
 */
export const triggerLocalDoseNotification = (medicineName, dosage, instructions, isPreReminder = false) => {
  if (!('Notification' in window) || Notification.permission !== 'granted') {
    return;
  }

  const title = isPreReminder 
    ? `⚠️ Medication Approaching: ${medicineName}`
    : `⏰ Time to take: ${medicineName}`;

  const body = isPreReminder
    ? `Your medicine dose of ${dosage} is scheduled in 1 minute. Let's get it ready.`
    : `Dose: ${dosage}. Instructions: ${instructions}. Mark as 'Taken' on your dashboard now.`;

  new Notification(title, {
    body,
    icon: '/ic_launcher_round.png',
    tag: `medication-${medicineName}`,
    requireInteraction: !isPreReminder // keeps exact reminders persistent
  });
};
