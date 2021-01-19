package ir.ha.avanghtodo.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

public class UtilClass {


    public static void setUnderLineForTextView(TextView tv, String text) {
        SpannableString ss = new SpannableString(text);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        ss.setSpan(underlineSpan,0,text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(text);
    }
}
