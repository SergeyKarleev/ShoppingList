package ru.sergeykarleev.shoppinglist.dialogues;

import ru.sergeykarleev.shoppinglist.R;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyFragmentDialogTemplate extends DialogFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().setTitle("Ёкспорт списка");
		View v = inflater.inflate(R.layout.dialog_transfer, container);
		return v;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		dismiss();
	}
}
