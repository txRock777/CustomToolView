package com.tx.toolview.edit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tx.toolview.R;
import com.tx.toolview.util.EditTextUtil;


/**
 * Created by  Rock
 * Create Date 2019/11/20 10:17
 * Used
 */
public class CustomFrameLayout extends FrameLayout {
    /*
     * 定义属性变量
     * */
    private Paint mPaint; // 画笔
    private boolean is_code,is_hint;
    // 分割线变量
    private int lineColor_click,lineColor_unclick;// 点击时 & 未点击颜色
    private int color;
    private int linePosition;
    private Context mContext;
    private View mView;
    //输入框
    private int  ic_deleteResID; // 删除图标 资源ID
    private Drawable ic_delete; // 删除图标
    private int delete_x,delete_y,delete_width,delete_height; // 删除图标起点(x,y)、删除图标宽、高（px）
    private int  ic_left_clickResID,ic_left_unclickResID;    // 左侧图标 资源ID（点击 & 无点击）
    private Drawable  ic_left_click,ic_left_unclick; // 左侧图标（点击 & 未点击）
    private int left_x,left_y,left_width,left_height; // 左侧图标起点（x,y）、左侧图标宽、高（px）
    private boolean is_left,is_right;//是否显示左边图标，是否显示删除图标
    private int cursor; // 光标
    private String etHint;
    private int etSize,etHintColor,etTextColor;
    private Drawable etBackground;
    private int  etBackgroundResID; // 资源ID
    private int  etMaxLines;
    //提示文本
    private String txHintText;
    private int txHintTextColor,txHintSize;
    //验证码文本
    private int txCodeSize,txCodeTextColor;
    private Drawable txCodeBackground;
    private int  txCodeBackgroundResID; // 资源ID

    private TextView tv_hint;
    private EditText et_content;
    private TextView send_code;
    private OnClickListener onClickListener;

    private int    errorLineColor;
    private String isNull;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    setFocusable(true);
                    setFocusableInTouchMode(true);
                    requestFocus();
                    color = errorLineColor;
                    mPaint.setColor(color);
                    break;
            }

        }
    };

    public CustomFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        init(context,attrs);
    }

    public Handler getHandleLiner(){
        return handler;
    }

    public void setCodeOnlickListener(OnClickListener onlickListener) {
        this.onClickListener = onlickListener;
        send_code.setOnClickListener(onlickListener);
    }

    public void setSendCode(String sendCode){
        send_code.setText(sendCode);
    }

    public void setSendCodeTextColor(int codeTextColor){
        send_code.setTextColor(codeTextColor);
    }

    public void setSendCodeClickable(boolean codeClickable){
        send_code.setClickable(codeClickable);
    }

    public void setSendCodeBg(int background){
        txCodeBackground = getResources().getDrawable(background);
        send_code.setBackground(txCodeBackground);
    }

    public boolean getIsNull(){
        if(TextUtils.isEmpty(et_content.getText().toString())){
            return true;
        }
        return false;
    }

    public void setInputType(int inputType){
        et_content.setInputType(inputType);
    }

    /**
     * 步骤1：初始化属性
     *
     */
    private void init(Context context, AttributeSet attrs){
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.item_menu_edit_layout, this, true);
        tv_hint = (TextView) mView.findViewById(R.id.tv_hint);
        et_content = (EditText) mView.findViewById(R.id.et_content);
        send_code = (TextView) mView.findViewById(R.id.send_code);
        // 获取Frame控件资源
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomFrameLayout);
        // 获取EditText控件资源
        TypedArray editTypedArray = context.obtainStyledAttributes(attrs,R.styleable.CustomEditText);

        /**
         * 是否显示验证码框，是否显示左边图标，是否显示删除图标(默认为false)
         */
        is_code  = typedArray.getBoolean(R.styleable.CustomFrameLayout_frame_is_code,false);
        is_hint  = typedArray.getBoolean(R.styleable.CustomFrameLayout_frame_is_hint,false);
        /**
         * 文本输入框
         */
        etHint = typedArray.getString(R.styleable.CustomFrameLayout_frame_et_hint);
        etSize = typedArray.getInteger(R.styleable.CustomFrameLayout_frame_et_size,0);
        etHintColor = typedArray.getInteger(R.styleable.CustomFrameLayout_frame_et_hint_color,0);
        // 1. 获取资源ID
        etBackgroundResID = typedArray.getResourceId(R.styleable.CustomFrameLayout_frame_et_background,R.color.white);
        // 2. 根据资源ID获取图标资源（转化成Drawable对象）
        etBackground = getResources().getDrawable(etBackgroundResID);
        etTextColor = typedArray.getInteger(R.styleable.CustomFrameLayout_frame_et_textColor,0);
        etMaxLines = typedArray.getInteger(R.styleable.CustomFrameLayout_frame_et_maxLines,0);

        is_left  = editTypedArray.getBoolean(R.styleable.CustomEditText_edit_is_left,false);
        is_right  = editTypedArray.getBoolean(R.styleable.CustomEditText_edit_is_right,false);

        /**
         * 初始化左侧图标（点击 & 未点击）
         */
        // a. 点击状态的左侧图标
        // 1. 获取资源ID
        ic_left_clickResID = editTypedArray.getResourceId(R.styleable.CustomEditText_edit_ic_left_click,R.mipmap.ic_left_click);
        // 2.根据资源ID获取图标资源（转化成Drawable对象）
        ic_left_click = getResources().getDrawable(ic_left_clickResID);
        // 3. 设置图标大小
        // 起点(x，y)、宽= left_width、高 = left_height
        left_x = editTypedArray.getInteger(R.styleable.CustomEditText_edit_left_x,0);
        left_y = editTypedArray.getInteger(R.styleable.CustomEditText_edit_left_y,0);
        left_width = editTypedArray.getInteger(R.styleable.CustomEditText_edit_left_width,0);
        left_height = editTypedArray.getInteger(R.styleable.CustomEditText_edit_left_height,0);

        // Drawable.setBounds(x,y,width,height) = 设置Drawable的初始位置、宽和高等信息
        // x = 组件在容器X轴上的起点、y = 组件在容器Y轴上的起点、width=组件的长度、height = 组件的高度
        ic_left_click.setBounds(left_x,left_y,left_width,left_height);

        // b. 未点击状态的左侧图标
        // 1. 获取资源ID
        ic_left_unclickResID = editTypedArray.getResourceId(R.styleable.CustomEditText_edit_ic_left_unclick,R.mipmap.ic_left_unclick);
        // 2. 根据资源ID获取图标资源（转化成Drawable对象）
        // 3. 设置图标大小（此处默认左侧图标点解 & 未点击状态的大小相同）
        ic_left_unclick = getResources().getDrawable(ic_left_unclickResID);
        ic_left_unclick.setBounds(left_x,left_y,left_width,left_height);

        /**
         * 初始化删除图标
         */
        // 1. 获取资源ID
        ic_deleteResID = editTypedArray.getResourceId(R.styleable.CustomEditText_edit_ic_delete,R.mipmap.delete);
        // 2. 根据资源ID获取图标资源（转化成Drawable对象）
        ic_delete = getResources().getDrawable(ic_deleteResID);
        // 3. 设置图标大小
        // 起点(x，y)、宽= left_width、高 = left_height
        delete_x = editTypedArray.getInteger(R.styleable.CustomEditText_edit_delete_x,0);
        delete_y = editTypedArray.getInteger(R.styleable.CustomEditText_edit_delete_y,0);
        delete_width = editTypedArray.getInteger(R.styleable.CustomEditText_edit_delete_width,0);
        delete_height = editTypedArray.getInteger(R.styleable.CustomEditText_edit_delete_height,0);
        ic_delete.setBounds(delete_x,delete_y,delete_width,delete_height);
        /**
         * 设置EditText左侧 & 右侧的图片（初始状态仅有左侧图片））
         * */
        if(is_left){
            et_content.setCompoundDrawables( ic_left_unclick, null,
                    null, null);
        }
        /**
         * 初始化光标（颜色 & 粗细）
         */
        cursor = typedArray.getResourceId(R.styleable.CustomEditText_edit_cursor, R.drawable.cursor);

        /**
         * 验证码
         */
        txCodeBackgroundResID = typedArray.getResourceId(R.styleable.CustomFrameLayout_frame_tx_code_background,R.drawable.verification_code_button_bg);
        txCodeBackground = getResources().getDrawable(txCodeBackgroundResID);
        txCodeSize = typedArray.getInteger(R.styleable.CustomFrameLayout_frame_tx_code_size,0);
        txCodeTextColor = typedArray.getInteger(R.styleable.CustomFrameLayout_frame_tx_code_textColor,0);
        /**
         * 提示字
         */
        txHintText = typedArray.getString(R.styleable.CustomFrameLayout_frame_tx_hint_text);
        txHintTextColor = typedArray.getInteger(R.styleable.CustomFrameLayout_frame_tx_hint_textColor,0);
        txHintSize = typedArray.getInteger(R.styleable.CustomFrameLayout_frame_tx_hint_size,0);

        /**
         * 初始化分割线（颜色、粗细、位置）
         */
        // 1. 设置画笔
        mPaint = new Paint();
        mPaint.setStrokeWidth(1.0f); // 分割线粗细

        // 2. 设置分割线颜色（使用十六进制代码，如#333、#8e8e8e）
        int lineColorClick_default = context.getResources().getColor(R.color.lineColor_click); // 默认 = 蓝色#1296db
        int lineColorunClick_default = context.getResources().getColor(R.color.lineColor_unclick); // 默认 = 灰色#9b9b9b
        lineColor_click = typedArray.getColor(R.styleable.CustomFrameLayout_frame_lineColor_click, lineColorClick_default);
        lineColor_unclick = typedArray.getColor(R.styleable.CustomFrameLayout_frame_lineColor_unclick, lineColorunClick_default);
        errorLineColor  = typedArray.getColor(R.styleable.CustomFrameLayout_frame_errorLineColor, context.getResources().getColor(R.color.red));
        color = lineColor_unclick;

        mPaint.setColor(lineColor_unclick); // 分割线默认颜色 = 灰色

        // 3. 分割线位置
        linePosition = typedArray.getInteger(R.styleable.CustomFrameLayout_frame_linePosition, 1);

        txCode();
        txHintInfo();
        etInfo();

        invalidate();
    }

    /**
     * 输入框
     */
    @SuppressLint("ClickableViewAccessibility")
    private void etInfo(){
        et_content.setHint(etHint);
        et_content.setTextSize(etSize);
        et_content.setHintTextColor(etHintColor);
        et_content.setTextColor(etTextColor);
        EditTextUtil.setCursorDrawableColor(et_content, cursor);
        if(etBackgroundResID != 0){
            et_content.setBackground(etBackground);
        }else {
            et_content.setBackground(null);
        }
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setDeleteIconVisible(s.length() > 0,hasFocus(),"onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_content.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // 原理：当手指抬起的位置在删除图标的区域，即视为点击了删除图标 = 清空搜索框内容
                switch (event.getAction()) {
                    // 判断动作 = 手指抬起时
                    case MotionEvent.ACTION_UP:
                        Drawable drawable =  ic_delete;

                        if (drawable != null && event.getX() <= (getWidth() - getPaddingRight())
                                && event.getX() >= (getWidth() - getPaddingRight() - drawable.getBounds().width())) {

                            // 判断条件说明
                            // event.getX() ：抬起时的位置坐标
                            // getWidth()：控件的宽度
                            // getPaddingRight():删除图标图标右边缘至EditText控件右边缘的距离
                            // 即：getWidth() - getPaddingRight() = 删除图标的右边缘坐标 = X1
                            // getWidth() - getPaddingRight() - drawable.getBounds().width() = 删除图标左边缘的坐标 = X2
                            // 所以X1与X2之间的区域 = 删除图标的区域
                            // 当手指抬起的位置在删除图标的区域（X2=<event.getX() <=X1），即视为点击了删除图标 = 清空搜索框内容
                            et_content.setText("");
                        }
                        break;
                }
                return false;
            }
        });
        et_content.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText editText = (EditText)view;
                setDeleteIconVisible( editText.getText().length() > 0,b,"onFocusChanged");
//                Log.i("Unnamed-iiiii","editText.getText().length():"+editText.getText().length());
            }
        });
    }

    /**
     * 提示文本
     */
    private void txHintInfo(){
        tv_hint.setText(txHintText);
        tv_hint.setTextColor(txHintTextColor);
        tv_hint.setTextSize(txHintSize);
        //是否显示提示文字
        if(is_hint){
            tv_hint.setVisibility(VISIBLE);
        }else {
            tv_hint.setVisibility(GONE);
        }
    }

    /**
     * 验证码
     */
    private void txCode(){
        send_code.setBackground(txCodeBackground);
        send_code.setTextSize(txCodeSize);
        send_code.setTextColor(txCodeTextColor);
        //是否显示验证码框
        if(is_code){
            send_code.setVisibility(VISIBLE);
        }else {
            send_code.setVisibility(GONE);
        }
    }

    /**
     * 关注1
     * 作用：判断是否显示删除图标 & 设置分割线颜色
     */
    private void setDeleteIconVisible(boolean deleteVisible,boolean leftVisible,String on) {
//        Log.i("Unnamed-iiiii","on:"+on);
//        Log.i("Unnamed-iiiii","deleteVisible:"+deleteVisible);
        if(is_left){
            et_content.setCompoundDrawables(leftVisible ?  ic_left_click :  ic_left_unclick, null,
                    deleteVisible ?  ic_delete: null, null);
        }
        if(is_right){
            et_content.setCompoundDrawables(null, null,
                    deleteVisible ?  ic_delete: null, null);
        }
        if("onFocusChanged".equals(on)){
            if(deleteVisible){
                color = lineColor_click;
            }else {
                color = lineColor_unclick;
            }
        }
        if("onTextChanged".equals(on)){
            if(deleteVisible){
                color = lineColor_click;
            }else {
                color = lineColor_unclick;
            }
        }
    }


    private  InputFilter[] getInputFilter(int lengInfo){
        InputFilter[] textFilters = new InputFilter[1];
        textFilters[0] = new InputFilter.LengthFilter(lengInfo) {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                return super.filter(source, start, end, dest, dstart, dend);
            }
        };
        return textFilters;
    }

    /**
     * 作用：绘制分割线
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(color);
        // 绘制分割线
        // 需要考虑：当输入长度超过输入框时，所画的线需要跟随着延伸
        // 解决方案：线的长度 = 控件长度 + 延伸后的长度
        int x=this.getScrollX(); // 获取延伸后的长度
        int w=this.getMeasuredWidth(); // 获取控件长度
        if(etMaxLines <= 0){
            et_content.setFilters(getInputFilter(w));
        }else {
            et_content.setFilters(getInputFilter(etMaxLines));
        }
        // 传入参数时，线的长度 = 控件长度 + 延伸后的长度
        canvas.drawLine(0, this.getMeasuredHeight()- linePosition, w+x,
                this.getMeasuredHeight() - linePosition, mPaint);
    }

}
