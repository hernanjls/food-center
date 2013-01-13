package foodcenter.android.service.msg;

import android.app.Activity;
import android.content.DialogInterface;
import android.widget.EditText;

public class OnClickAddMsg implements DialogInterface.OnClickListener
{

    private final Activity owner;
    private final EditText msg;

    public OnClickAddMsg(Activity owner, EditText msg)
    {
        this.owner = owner;
        this.msg = msg;
    }

    @Override
    public void onClick(DialogInterface dialog, int id)
    {
        String m = msg.getText().toString();
        new MsgAddAsyncTask(owner, m).execute();
    }

}
