package com.app_republic.bottle.util;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.app_republic.bottle.model.Comment;
import com.app_republic.bottle.model.Feeling;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat formatterYear = new SimpleDateFormat("MM/dd/yyyy");

    public static String getCommentDate(Long timeAtMiliseconds) {

        if (timeAtMiliseconds == 0) {
            return "";
        }

        //API.log("Day Ago "+dayago);
        String result = "now";
        String dataSot = formatter.format(new Date());
        Calendar calendar = Calendar.getInstance();

        long dayagolong = timeAtMiliseconds;
        calendar.setTimeInMillis(dayagolong);
        String agoformater = formatter.format(calendar.getTime());

        Date CurrentDate = null;
        Date CreateDate = null;

        try {
            CurrentDate = formatter.parse(dataSot);
            CreateDate = formatter.parse(agoformater);

            long different = Math.abs(CurrentDate.getTime() - CreateDate.getTime());

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            if (elapsedDays == 0) {
                if (elapsedHours == 0) {
                    if (elapsedMinutes == 0) {
                        if (elapsedSeconds < 0) {
                            return "0" + " s";
                        } else {
                            if (elapsedSeconds > 0 && elapsedSeconds < 59) {
                                return "now";
                            }
                        }
                    } else {
                        return String.valueOf(elapsedMinutes) + "m ago";
                    }
                } else {
                    return String.valueOf(elapsedHours) + "h ago";
                }

            } else {
                if (elapsedDays <= 29) {
                    return String.valueOf(elapsedDays) + "d ago";
                }
                if (elapsedDays > 29 && elapsedDays <= 58) {
                    return "1Mth ago";
                }
                if (elapsedDays > 58 && elapsedDays <= 87) {
                    return "2Mth ago";
                }
                if (elapsedDays > 87 && elapsedDays <= 116) {
                    return "3Mth ago";
                }
                if (elapsedDays > 116 && elapsedDays <= 145) {
                    return "4Mth ago";
                }
                if (elapsedDays > 145 && elapsedDays <= 174) {
                    return "5Mth ago";
                }
                if (elapsedDays > 174 && elapsedDays <= 203) {
                    return "6Mth ago";
                }
                if (elapsedDays > 203 && elapsedDays <= 232) {
                    return "7Mth ago";
                }
                if (elapsedDays > 232 && elapsedDays <= 261) {
                    return "8Mth ago";
                }
                if (elapsedDays > 261 && elapsedDays <= 290) {
                    return "9Mth ago";
                }
                if (elapsedDays > 290 && elapsedDays <= 319) {
                    return "10Mth ago";
                }
                if (elapsedDays > 319 && elapsedDays <= 348) {
                    return "11Mth ago";
                }
                if (elapsedDays > 348 && elapsedDays <= 360) {
                    return "12Mth ago";
                }

                if (elapsedDays > 360 && elapsedDays <= 720) {
                    return "1 year ago";
                }

                if (elapsedDays > 720) {
                    Calendar calendarYear = Calendar.getInstance();
                    calendarYear.setTimeInMillis(dayagolong);
                    return formatterYear.format(calendarYear.getTime()) + "";
                }

            }

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getCommentIndexById(List<Comment> comments, String id) {
        for (Comment comment : comments)
            if (comment.getId().equals(id))
                return comments.indexOf(comment);

        return -1;

    }

    public static int getFeelingIndexById(ArrayList<Feeling> feelings, String id) {
        for (Feeling feeling : feelings)
            if (feeling.getId().equals(id))
                return feelings.indexOf(feeling);

        return -1;

    }

    public static void updateFeelings(Context context, Comment comment, View view, boolean state) {

        FirebaseFirestore db = AppSingleton.getInstance(context).getDb();
        DocumentReference feelingRef = db.collection("feelings")
                .document(AppSingleton.getInstance(context).getFirebaseAuth().getUid()).collection("topics")
                .document(comment.getId());
        DocumentReference commentRef = db.collection("comments")
                .document(comment.getId());


        db.runTransaction(transaction -> {
            DocumentSnapshot commentSnapshot = transaction.get(commentRef);
            DocumentSnapshot feelingSnapshot = transaction.get(feelingRef);


            int increment = 1;
            boolean switchedFeeling = false;
            if (feelingSnapshot.exists()) {
                if (feelingSnapshot.getBoolean("state") == state) {
                    transaction.delete(feelingRef);
                    increment = -1;
                } else {
                    switchedFeeling = true;
                    transaction.update(feelingRef, "state", state);
                }
            } else {

                Feeling feeling = new Feeling(state, comment.getTargetType(),
                        comment.getTargetId(), comment.getId());

                transaction.set(feelingRef, feeling);
            }


            if (state) {
                double newLikes = commentSnapshot.getLong("likes") + increment;
                transaction.update(commentRef, "likes", newLikes);
                if (switchedFeeling) {
                    double newDislikes = commentSnapshot.getLong("dislikes") - 1;
                    transaction.update(commentRef, "dislikes", newDislikes);
                }
            } else {
                double newDislikes = commentSnapshot.getLong("dislikes") + increment;
                transaction.update(commentRef, "dislikes", newDislikes);
                if (switchedFeeling) {
                    double newLikes = commentSnapshot.getLong("likes") - 1;
                    transaction.update(commentRef, "likes", newLikes);
                }

            }

            if (increment == -1)
                return -1;
            else if (state)
                return 1;
            else return 0;

        }).addOnSuccessListener(result -> {
            view.setEnabled(true);
            comment.setLike(result.intValue());
        })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    view.setEnabled(true);
                });


    }

}
