package Library;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import Models.DirectionCreation;

/**
 * Created by rikardolsson on 2016-11-22.
 */

public class DirectionEditText extends EditText {

    // Model bind to EditText
    private DirectionCreation direction;
    private TextWatcher textWatcher;

    public DirectionEditText(Context context) {
        super(context);
    }

    public DirectionEditText(Context context, DirectionCreation direction) {
        super(context);
        this.direction = direction;
    }

    public DirectionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DirectionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DirectionEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // Setting super.setText AND models text
    public void setDirection(DirectionCreation d) {
        this.direction = d;
        this.setText(d.description);

        this.removeTextChangedListener(this.textWatcher);

        this.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // NOT NEEDED
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // When EditText.text is changed, add it to model
                direction.description = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // NOT NEEDED
            }
        };

        this.addTextChangedListener(this.textWatcher);
    }
}
