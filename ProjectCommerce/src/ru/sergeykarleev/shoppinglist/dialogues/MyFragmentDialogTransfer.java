package ru.sergeykarleev.shoppinglist.dialogues;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.activities.MainActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class MyFragmentDialogTransfer extends DialogFragment  implements OnClickListener{

	MainActivity mActivity;
	Button btnExportTemplate;	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().setTitle("Ёкспорт списка");
		View v = inflater.inflate(R.layout.dialog_transfer, container);
		mActivity = (MainActivity) getActivity();
		
		btnExportTemplate = (Button) v.findViewById(R.id.btnExportTemplate);
		
		return v;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnExportTemplate:
			break;

		default:
			break;
		}
	}
	
	

}
