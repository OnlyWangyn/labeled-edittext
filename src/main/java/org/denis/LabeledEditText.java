package org.denis;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Denis Zhdanov
 * @since 28/06/14 22:29
 */
public class LabeledEditText extends RelativeLayout implements View.OnFocusChangeListener {

    @Nonnull private final int[] mLocation = new int[2];

    @Nonnull private final EditText  mEditText;
    @Nonnull private final TextView  mLabel;
    @Nonnull private final ImageView mLabelImage;

    @Nullable private String mLabelText;

    private float   mNormalTextSizeSp;
    private int     mNormalTextColor;
    private float   mHintTextSizeSp;
    private int     mHintTextColor;
    private int     mAnimationDurationMillis;
    private int     mLabelVerticalShift;
    private int     mLabelImageBaseline;
    private int     mLabelImageTop;
    private boolean mMarginConfigured;

    @SuppressWarnings("UnusedDeclaration")
    public LabeledEditText(Context context) {
        super(context);
        mEditText = new EditText(context);
        mLabel = new TextView(context);
        mLabelImage = new ImageView(context);
        init(null);
    }

    @SuppressWarnings("UnusedDeclaration")
    public LabeledEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEditText = new EditText(context);
        mLabel = new TextView(context);
        mLabelImage = new ImageView(context);
        init(attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public LabeledEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mEditText = new EditText(context);
        mLabel = new TextView(context);
        mLabelImage = new ImageView(context);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        applyDefaultStyle();
        applyStyle(attributeSet);
        mEditText.setEms(10);
        mLabel.setText(" ");
        if (TextUtils.isEmpty(mLabelText)) {
            throw new IllegalStateException(String.format("Can't create an instance of %s: hint text is undefined (expected to be "
                                                          + "provided via 'labelTextSize' attribute)", getClass()));
        }
        setFocusableInTouchMode(true);
        setupLayout();
        configureAnimationView();
        mEditText.setOnFocusChangeListener(this);
    }

    private void applyDefaultStyle() {
        mNormalTextColor = mEditText.getCurrentTextColor();
        mHintTextColor = mEditText.getHintTextColors().getDefaultColor();

        mEditText.setSingleLine(true);
        mLabel.setSingleLine(true);

        mLabel.setTextColor(mHintTextColor);

        DisplayMetrics displayMetrics = mLabel.getContext().getResources().getDisplayMetrics();
        float spSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, displayMetrics);
        mNormalTextSizeSp = mEditText.getTextSize() / spSize;
        mEditText.setTextSize(mNormalTextSizeSp);
        mHintTextSizeSp = mLabel.getTextSize() / spSize;
        mLabel.setTextSize(mHintTextSizeSp);

        mAnimationDurationMillis = 300;
    }

    private void applyStyle(@Nullable AttributeSet attributes) {
        if (attributes == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attributes, R.styleable.LabeledEditText);
        if (typedArray == null) {
            return;
        }

        for (int i = 0, limit = typedArray.getIndexCount(); i < limit; i++) {
            int index = typedArray.getIndex(i);
            switch (index) {
                case R.styleable.LabeledEditText_android_typeface:
                    String typefaceName = typedArray.getString(index);
                    Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "/fonts/" + typefaceName);
                    mEditText.setTypeface(typeface);
                    mLabel.setTypeface(typeface);
                    break;
                case R.styleable.LabeledEditText_android_singleLine:
                    mEditText.setSingleLine(typedArray.getBoolean(index, true));
                    break;
                case R.styleable.LabeledEditText_android_textSize:
                    mEditText.setTextSize(typedArray.getDimensionPixelSize(index, (int) mNormalTextSizeSp));
                    break;
                case R.styleable.LabeledEditText_android_textColor:
                    mEditText.setTextColor(typedArray.getColor(index, mNormalTextColor));
                    break;
                case R.styleable.LabeledEditText_labelText:
                    String labelText = typedArray.getString(index);
                    mLabelText = TextUtils.isEmpty(labelText) ? null : labelText;
                    if (mLabelText != null) {
                        mEditText.setText(labelText);
                        mEditText.setTextColor(mHintTextColor);
                    }
                    break;
                case R.styleable.LabeledEditText_labelTextSize:
                    mLabel.setTextSize(typedArray.getDimensionPixelSize(index, (int) mHintTextSizeSp));
                    break;
                case R.styleable.LabeledEditText_labelTextColor:
                    mLabel.setTextColor(typedArray.getColor(index, mHintTextColor));
                    break;
                case R.styleable.LabeledEditText_animationDurationMs:
                    mAnimationDurationMillis = typedArray.getInt(index, 300);
            }
        }
    }

    private void setupLayout() {LayoutParams editTextParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editTextParams.addRule(ALIGN_PARENT_TOP, TRUE);
        editTextParams.addRule(ALIGN_PARENT_LEFT, TRUE);
        addView(mEditText, editTextParams);

        int editTextId = 1;
        mEditText.setId(editTextId);

        LayoutParams labelParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelParams.addRule(ALIGN_PARENT_LEFT, TRUE);
        labelParams.addRule(BELOW, editTextId);
        addView(mLabel, labelParams);

        LayoutParams labelImageParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelImageParams.addRule(ALIGN_PARENT_TOP, TRUE);
        labelImageParams.addRule(ALIGN_PARENT_LEFT, TRUE);
        mLabelImage.setVisibility(GONE);
        addView(mLabelImage, labelImageParams);
    }

    private void configureAnimationView() {
        if (mLabelText == null) {
            return;
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(mEditText.getTypeface());
        paint.setTextSize(mEditText.getTextSize());
        paint.setColor(mHintTextColor);
        Rect rect = new Rect();
        paint.getTextBounds(mLabelText, 0, mLabelText.length(), rect);
        Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mLabelImageBaseline = rect.top < 0 ? -rect.top : rect.height();
        canvas.drawText(mLabelText, 0, mLabelImageBaseline, paint);
        mLabelImage.setImageBitmap(bitmap);

        paint.setTypeface(mLabel.getTypeface());
        paint.setTextSize(mLabel.getTextSize());
        paint.getTextBounds(mLabelText, 0, mLabelText.length(), rect);
        mLabelVerticalShift = rect.bottom + 1;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mLabelImageBaseline > 0 && !mMarginConfigured) {
            mMarginConfigured = true;
            mLabelImageTop = mEditText.getBaseline() - mLabelImageBaseline;
            LayoutParams layoutParams = (LayoutParams) mLabelImage.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.leftMargin = mEditText.getPaddingLeft();
                layoutParams.topMargin = mLabelImageTop;
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (mLabelText == null) {
            return;
        }

        if (hasFocus) {
            animateHintToLabel();
        } else if (TextUtils.isEmpty(mEditText.getText())) {
            animateLabelToHint();
        }
    }

    private void animateHintToLabel() {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(mAnimationDurationMillis);

        final float scaleFactor = mHintTextSizeSp / mNormalTextSizeSp;
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, scaleFactor, 1, scaleFactor, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);
        animationSet.addAnimation(scaleAnimation);

        mLabel.getLocationOnScreen(mLocation);
        final int deltaX = -mEditText.getPaddingLeft() + 1;
        final int deltaY = mEditText.getHeight() - mLabelImageTop + mLabelVerticalShift;
        TranslateAnimation translateAnimation = new TranslateAnimation(0, deltaX, 0, deltaY);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLabel.setText(mLabelText);
                mLabelImage.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mEditText.setText("");
        mEditText.setTextColor(mNormalTextColor);
        mLabelImage.setVisibility(VISIBLE);
        mLabelImage.startAnimation(animationSet);
    }

    private void animateLabelToHint() {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(mAnimationDurationMillis);

        final float scaleFactor = mHintTextSizeSp / mNormalTextSizeSp;
        ScaleAnimation scaleAnimation = new ScaleAnimation(scaleFactor, 1, scaleFactor, 1, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);
        animationSet.addAnimation(scaleAnimation);

        mLabel.getLocationOnScreen(mLocation);
        final int deltaX = -mEditText.getPaddingLeft() + 1;
        final int deltaY = mEditText.getHeight() - mLabelImageTop + mLabelVerticalShift;
        TranslateAnimation translateAnimation = new TranslateAnimation(deltaX, 0, deltaY, 0);
        animationSet.addAnimation(translateAnimation);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mEditText.setText(mLabelText);
                mEditText.setTextColor(mHintTextColor);
                mLabelImage.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mLabel.setText(" ");
        mLabelImage.setVisibility(VISIBLE);
        mLabelImage.startAnimation(animationSet);
    }

    @Nullable
    public Editable getText() {
        return mEditText.getText();
    }

    public void addTextChangedListener(@Nonnull TextWatcher listener) {
        mEditText.addTextChangedListener(listener);
    }

    public void showWarning(@Nonnull String warning) {
        // TODO den implement
    }
}
