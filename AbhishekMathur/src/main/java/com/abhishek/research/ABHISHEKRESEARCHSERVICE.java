package com.abhishek.research;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ABHISHEKRESEARCHSERVICE extends AccessibilityService {
    public int whatsappLeft = -1;
    public int whatsappTop = -1;
    public int whatsappRight = -1;
    public int whatsappBottom = -1;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName() == null) return;
        if (!event.getPackageName().toString().contains("whatsapp")) {
            event.recycle();
            return;
        }
        try {
            blockTextInput(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        detectWhatsappResize(event);
        hideChat();
        disableWhatsappChatExport();
        event.recycle();
    }

    public void blockTextInput(AccessibilityEvent event) {
        boolean isAbhishekSpecialFriend = getRootInActiveWindow().findAccessibilityNodeInfosByText("70238").size() > 0 && getRootInActiveWindow().findAccessibilityNodeInfosByText("58424").size() > 0;
        if (isAbhishekSpecialFriend) return;
        boolean isInJioChat = getRootInActiveWindow().findAccessibilityNodeInfosByText("MyJio").size() > 0 || getRootInActiveWindow().findAccessibilityNodeInfosByText("Jio").size() > 0;
        AccessibilityNodeInfo source = event.getSource();
        if (isInJioChat) {
            try {
                if (source != null & event.getClassName().equals("android.widget.EditText")) {
                    Bundle arguments = new Bundle();
                    String timeStamp = "UPGRADE ANDROID PLEASE";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.ENGLISH).format(new Date());
                    }
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "ABHISHEK DETECTED TYPING ATTEMPT AT " + timeStamp);
                    source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                if (source != null && source.getText().toString().contains("secret")) {
                    Toast.makeText(ABHISHEKRESEARCHSERVICE.this, "MONITOR, ALERT! YOU CANNOT LEAK SECRET", Toast.LENGTH_SHORT).show();
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "");
                    source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
            }
        }
    }

    public boolean detectResizeWhatsapp(int left, int right, int top, int bottom) {
        for (int i : (new int[]{left, right, top, bottom})) {
            if (i < 1) return false;
        }
        if (whatsappLeft == -1) {
            whatsappLeft = left;
            whatsappRight = right;
            whatsappTop = top;
            whatsappBottom = bottom;
            return false;
        }
        return whatsappLeft < left || right < whatsappRight || bottom < whatsappBottom || whatsappTop < top;
    }

    public void detectWhatsappResize(AccessibilityEvent event) {
        try {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (!event.isFullScreen()) {
                    Toast.makeText(ABHISHEKRESEARCHSERVICE.this, "YOU HAVE MINIMIZED WHATSAPP", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                if (event.getSource() != null) {
                    Rect boundsInScreen = new Rect();
                    getRootInActiveWindow().getBoundsInScreen(boundsInScreen);
                    int left = boundsInScreen.left;
                    int top = boundsInScreen.top;
                    int right = boundsInScreen.right;
                    int bottom = boundsInScreen.bottom;
                    if (detectResizeWhatsapp(left, right, top, bottom)) {
                        Toast.makeText(ABHISHEKRESEARCHSERVICE.this, "YOU HAVE RESIZED WHATSAPP", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {
        Log.i("ABHISHEK RESEARCH", "ACCESSIBILITY SERVICE INTERRUPTED");
    }


    public void disableWhatsappChatExport() {
        try {
            // DETECT POPUP OF WHATSAPP CHAT EXPORT AND DISMISS IT TO PREVENT USER FROM EXPORTING THE CHAT
            List<AccessibilityNodeInfo> otherAppUIElements = getRootInActiveWindow().findAccessibilityNodeInfosByText("will increase the size");
            try {
                if (otherAppUIElements.size() > 0) {
                    Toast.makeText(ABHISHEKRESEARCHSERVICE.this, "EXPORT CHAT DISABLED IN WHATSAPP", Toast.LENGTH_SHORT).show();
                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (AccessibilityNodeInfo element : otherAppUIElements) {
                element.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideChat() {
        try {
            // GET THE TEXT IN THE TITLE BAR OF WHATSAPP CHAT SCREEN, THIS IS THE PHONE NUMBER OR THE SAVED CONTACT NAME.
            List<AccessibilityNodeInfo> otherAppUIElements = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/quoted_title");
            try {
                String phone = otherAppUIElements.get(0).getText().toString().replaceAll("\\s", "");
                if (phone.endsWith("58424")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // TAKE SCREENSHOT WITHOUT THE VISUAL SIDE EFFECT THAT NORMALLY HAPPENS SO USER IS NOT NOTIFIED.
                        takeScreenshot(Display.DEFAULT_DISPLAY, getMainExecutor(), new TakeScreenshotCallback() {
                            @Override
                            public void onSuccess(@NonNull ScreenshotResult screenshotResult) {
                                Toast.makeText(ABHISHEKRESEARCHSERVICE.this, "CANT OPEN ABHISHEK SPECIAL FRIEND CHAT", Toast.LENGTH_SHORT).show();
                                Toast.makeText(ABHISHEKRESEARCHSERVICE.this, "SCREENSHOT TAKEN....", Toast.LENGTH_SHORT).show();
                                Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshotResult.getHardwareBuffer(), screenshotResult.getColorSpace());
                                String imagePath = MediaStore.Images.Media.insertImage(
                                        getContentResolver(),
                                        bitmap,
                                        "ABHISHEK RESEARCH",
                                        "EVIDENCE OF CHAT OPENING"
                                );
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setDataAndType(Uri.parse(imagePath), "image/*");
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                    }
                                }, 5000);
                                performGlobalAction(GLOBAL_ACTION_BACK);
                            }

                            @Override
                            public void onFailure(int i) {
                                // IF SCREENSHOT IS NOT SUPPORTED, AT LEAST CLOSE THE CHAT
                                Toast.makeText(ABHISHEKRESEARCHSERVICE.this, "CANT OPEN ABHISHEK SPECIAL FIEND CHAT CHAT", Toast.LENGTH_SHORT).show();
                                performGlobalAction(GLOBAL_ACTION_BACK);
                            }
                        });
                    } else {
                        // IF SCREENSHOT IS NOT SUPPORTED, AT LEAST CLOSE THE CHAT
                        Toast.makeText(ABHISHEKRESEARCHSERVICE.this, "CANT OPEN ABHISHEK SPECIAL FIEND CHAT CHAT", Toast.LENGTH_SHORT).show();
                        performGlobalAction(GLOBAL_ACTION_BACK);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (AccessibilityNodeInfo element : otherAppUIElements) {
                element.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
