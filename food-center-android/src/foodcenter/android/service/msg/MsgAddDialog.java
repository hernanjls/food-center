//package foodcenter.android.service.msg;
//
//import foodcenter.android.MainActivity;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.util.Log;
//import android.widget.EditText;
//
//public class MsgAddDialog
//{
//    private final MainActivity owner;
//    
//    public MsgAddDialog(MainActivity owner)
//    {
//        this.owner = owner;
//        showAddMsgDialog();
//    }
//    
//    private void showAddMsgDialog()
//    {
//        EditText msgText = new EditText(owner);
//        final AlertDialog.Builder builder = new AlertDialog.Builder(owner);
//        builder.setMessage("add new msg:");
//        builder.setCancelable(true);
//        builder.setView(msgText);
//        String msg = msgText.getText().toString();
//        Log.i("msg", msg);
//        builder.setPositiveButton("Add Msg", new OnClickAddMsg(owner, msgText));  
//        builder.create().show();
//    }
//    
//    
//    class OnClickAddMsg implements DialogInterface.OnClickListener
//    {
//        private final EditText msg;
//
//        public OnClickAddMsg(Activity owner, EditText msg)
//        {
//            this.msg = msg;
//        }
//
//        @Override
//        public void onClick(DialogInterface dialog, int id)
//        {
//            String m = msg.getText().toString();
//            new MsgAddAsyncTask(owner, m).execute();
//        }
//
//    }
//}
//
