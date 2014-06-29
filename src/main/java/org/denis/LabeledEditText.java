package org.denis;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import bo.pic.android.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Denis Zhdanov
 * @since 28/06/14 22:29
 */
public class LabeledEditText extends LinearLayout {

    @Nonnull private final EditText mEditText;
    @Nonnull private final TextView mLabel;

    @Nullable private String mLabelText;
    private boolean mHasCustomText;

    @SuppressWarnings("UnusedDeclaration")
    public LabeledEditText(Context context) {
        super(context);
        mEditText = new EditText(context);
        mLabel = new TextView(context);
        init(null);
    }

    @SuppressWarnings("UnusedDeclaration")
    public LabeledEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEditText = new EditText(context);
        mLabel = new TextView(context);
        init(attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public LabeledEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mEditText = new EditText(context);
        mLabel = new TextView(context);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        applyDefaultStyle();
        applyStyle(attributeSet);
        setOrientation(VERTICAL);
        setFocusableInTouchMode(true);
        addView(mEditText, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mLabel, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mHasCustomText || mLabelText == null) {
                    return;
                }
                if (hasFocus) {
                    // TODO den implement
                    float from = mEditText.getTextSize();
                    float to = mLabel.getTextSize();
                    ScaleAnimation animation = new ScaleAnimation(from, to, from, to);
                    animation.setDuration(300);
                    mLabel.startAnimation(animation);
                } else {
                    // TODO den implement
                }
            }
        });
    }

    private void applyDefaultStyle() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        mEditText.setTypeface(typeface);
        mLabel.setTypeface(typeface);

        mEditText.setSingleLine(true);
        mLabel.setSingleLine(true);

        mEditText.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_large));
        mEditText.setTextColor(getResources().getColor(R.color.text_dark));

        mLabel.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_small));
        mLabel.setTextColor(getResources().getColor(R.color.text_field_desc));
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
                    float defaultTextSize = getResources().getDimension(R.dimen.text_size_large);
                    mEditText.setTextSize(typedArray.getDimensionPixelSize(index, (int) defaultTextSize));
                    break;
                case R.styleable.LabeledEditText_android_textColor:
                    mEditText.setTextColor(typedArray.getColor(index, R.color.text_dark));
                    break;
                case R.styleable.LabeledEditText_labelText:
                    String labelText = typedArray.getString(index);
                    mLabel.setText(labelText);
                    mLabelText = TextUtils.isEmpty(labelText) ? null : labelText;
                    break;
                case R.styleable.LabeledEditText_labelTextSize:
                    float defaultLabelTextSize = getResources().getDimension(R.dimen.text_size_small);
                    mLabel.setTextSize(typedArray.getDimensionPixelSize(index, (int) defaultLabelTextSize));
                    break;
                case R.styleable.LabeledEditText_labelTextColor:
                    mLabel.setTextColor(typedArray.getColor(index, R.color.text_field_desc));
            }
        }
    }

    @Nullable
    public Editable getText() {
        return mEditText.getText();
    }

    public void setText(@Nonnull CharSequence text) {
        mEditText.setText(text);
        mHasCustomText = !TextUtils.isEmpty(text);
        // TODO den show animation if the text is empty and label text is not.
    }

    public void addTextChangedListener(@Nonnull TextWatcher listener) {
        mEditText.addTextChangedListener(listener);
    }

    public void showWarning(@Nonnull String warning) {
        // TODO den implement
    }
}
