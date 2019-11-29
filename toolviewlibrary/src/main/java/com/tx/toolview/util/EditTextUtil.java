package com.tx.toolview.util;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by  Rock
 * Create Date 2019/11/27 14:31
 * Used
 */
public class EditTextUtil {
    /**
     * 代码设置光标颜色
     *
     * @param editText 你使用的EditText
     * @param color    光标颜色
     */
    public static void setCursorDrawableColor(EditText editText, int color) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");//获取这个字段
            fCursorDrawableRes.setAccessible(true);//代表这个字段、方法等等可以被访问
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);

            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);

//            Class<?> clazz = editor.getClass();
//            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            Class<?> editorClazz = Class.forName("android.widget.Editor");
            Field fCursorDrawable = editorClazz.getDeclaredField("mCursorDrawable");

            fCursorDrawable.setAccessible(true);

//            Drawable[] drawables = new Drawable[2];
//            drawables[0] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
//            drawables[1] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
//            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);//SRC_IN 上下层都显示。下层居上显示。
//            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
//            fCursorDrawable.set(editor, drawables);

            //根据光标的resid获取到相应的drawable
            Drawable cursorDrawable = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
            if (cursorDrawable == null) {
                return;
            }
            //然后给cursorDrawable着色
            Drawable tintDrawable  = tintDrawable(cursorDrawable, ColorStateList.valueOf(color));
            //把着色后的cursorDrawable给editor的mCursorDrawable数组
            Drawable[] drawables = new Drawable[] {tintDrawable, tintDrawable};
            fCursorDrawable.set(editor, drawables);


        } catch (Throwable ignored) {
            ignored.getMessage();
        }
    }

    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        //给相应的drawable着色（v4包中的DrawableCompat着色）
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

}
