package com.nibou.nibouexpert.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nibou.nibouexpert.R;

import java.io.Serializable;
import java.util.logging.Handler;

public class AppDialogs implements Serializable {

    private Dialog progressBar;
    private ProgressDialog progressDialog;
    private static AppDialogs instance = null;

    private Dialog fullScreenDialog;

    public interface DialogCallback {
        void response(boolean status);
    }

    public interface InputDialogCallback {
        void response(String result);
    }

    public interface ProgressDialogDissmissListener {
        void progressBarDismiss();

        void progressDialogDismiss();
    }

    private AppDialogs() {
    }

    public static AppDialogs getInstance() {
        if (instance == null) {
            instance = new AppDialogs();
        }
        return instance;
    }


    public void showProgressBar(Context context, final ProgressDialogDissmissListener progressDialogDissmissListener, boolean isShow) {
        try {
            if (context != null && !((AppCompatActivity) context).isFinishing()) {
                if (isShow) {
                    progressBar = new Dialog(context, android.R.style.Theme_Material_Dialog);
                    progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressBar.setContentView(R.layout.progress_bar_view);
                    progressBar.setCanceledOnTouchOutside(false);
                    ProgressBar progress = (ProgressBar) progressBar.findViewById(R.id.progressBar);
                    progress.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                if (progressBar != null) {
                    progressBar.setOnDismissListener(dialog -> {
                        if (progressDialogDissmissListener != null) {
                            progressDialogDissmissListener.progressBarDismiss();
                        }
                    });
                }
                try {
                    if (isShow) {
                        if (progressBar != null && !progressBar.isShowing())
                            progressBar.show();
                    } else {
                        if (progressBar != null && progressBar.isShowing())
                            progressBar.dismiss();
                    }
                } catch (WindowManager.BadTokenException e) {
                    progressBar = null;
                    if (isShow)
                        showProgressBar(context, progressDialogDissmissListener, isShow);
                }
            } else {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgressDialog(Context context, final ProgressDialogDissmissListener progressDialogDissmissListener, String message, boolean isShow) {
        if (context != null && !((AppCompatActivity) context).isFinishing()) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(message);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (progressDialogDissmissListener != null) {
                        progressDialogDissmissListener.progressDialogDismiss();
                    }
                }
            });
            try {
                if (isShow) {
                    if (progressDialog != null && !progressDialog.isShowing()) {
                        progressDialog.setMessage(message);
                        progressDialog.show();
                    }
                } else {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
                if (isShow) {
                    progressDialog = null;
                    showProgressDialog(context, progressDialogDissmissListener, message, isShow);
                }
            }
        } else {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    public void showConfirmCustomDialog(final Context context, String title, String message, String b1, String b2, final DialogCallback dialogCallback) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.findViewById(R.id.message).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.edittext).setVisibility(View.GONE);
        dialog.findViewById(R.id.button).setVisibility(View.GONE);
        dialog.findViewById(R.id.button1).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.button2).setVisibility(View.VISIBLE);

        TextView titletext = dialog.findViewById(R.id.title);
        titletext.setText(title);
        if (title == null)
            titletext.setVisibility(View.INVISIBLE);

        TextView messagetext = dialog.findViewById(R.id.message);
        messagetext.setText(message);

        TextView button1 = dialog.findViewById(R.id.button1);
        button1.setText(b1);
        button1.setOnClickListener(v -> {
            dialog.dismiss();
            if (dialogCallback != null) {
                dialogCallback.response(false);
            }
        });

        TextView button2 = dialog.findViewById(R.id.button2);
        button2.setText(b2);
        button2.setOnClickListener(v -> {
            dialog.dismiss();
            if (dialogCallback != null) {
                dialogCallback.response(true);
            }
        });
        dialog.show();
    }

    public void showInfoWithOutTitleCustomDialog(final Context context, String title, String message, String buttonText, final DialogCallback dialogCallback) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.findViewById(R.id.message).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.edittext).setVisibility(View.GONE);
        dialog.findViewById(R.id.button1).setVisibility(View.GONE);
        dialog.findViewById(R.id.button2).setVisibility(View.GONE);
        dialog.findViewById(R.id.button).setVisibility(View.VISIBLE);


        TextView titletext = dialog.findViewById(R.id.title);
        titletext.setVisibility(View.GONE);
        titletext.setText(title);
        TextView messagetext = dialog.findViewById(R.id.message);
        messagetext.setText(message);
        TextView button = dialog.findViewById(R.id.button);
        button.setText(buttonText);
        button.setOnClickListener(v -> {
            dialog.dismiss();
            if (dialogCallback != null) {
                dialogCallback.response(true);
            }
        });
        dialog.show();
    }

    public void showInfoCustomDialog(final Context context, String title, String message, String buttonText, final DialogCallback dialogCallback) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.findViewById(R.id.message).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.edittext).setVisibility(View.GONE);
        dialog.findViewById(R.id.button1).setVisibility(View.GONE);
        dialog.findViewById(R.id.button2).setVisibility(View.GONE);
        dialog.findViewById(R.id.button).setVisibility(View.VISIBLE);

        TextView titletext = dialog.findViewById(R.id.title);
        titletext.setText(title);
        TextView messagetext = dialog.findViewById(R.id.message);
        messagetext.setText(message);
        TextView button = dialog.findViewById(R.id.button);
        button.setText(buttonText);
        button.setOnClickListener(v -> {
            dialog.dismiss();
            if (dialogCallback != null) {
                dialogCallback.response(true);
            }
        });
        dialog.show();
    }

    public void showInputCustomDialog(final Context context, String title, String buttonText, final InputDialogCallback dialogCallback) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.findViewById(R.id.button1).setVisibility(View.GONE);
        dialog.findViewById(R.id.button2).setVisibility(View.GONE);
        dialog.findViewById(R.id.message).setVisibility(View.INVISIBLE);
        dialog.findViewById(R.id.edittext).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.button).setVisibility(View.VISIBLE);

        final TextView edittext = dialog.findViewById(R.id.edittext);
        TextView titletext = dialog.findViewById(R.id.title);
        titletext.setText(title);
        TextView button = dialog.findViewById(R.id.button);
        button.setText(buttonText);
        button.setOnClickListener(v -> {
            dialog.dismiss();
            if (dialogCallback != null) {
                dialogCallback.response(edittext.getText().toString());
            }
        });
        dialog.show();
    }

    public void showFullScreenImage(Context context, String path) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_fullscreen);
        ImageView fullImageView = (ImageView) dialog.findViewById(R.id.fullImageView);
        final View progress_bar = dialog.findViewById(R.id.progress_bar);
        Glide.with(context)
                .load(path)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progress_bar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progress_bar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .fitCenter().into(fullImageView);
        dialog.show();
    }
}
