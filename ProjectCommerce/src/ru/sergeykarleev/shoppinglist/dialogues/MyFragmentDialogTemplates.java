package ru.sergeykarleev.shoppinglist.dialogues;

import java.util.Map;

import ru.sergeykarleev.shoppinglist.R;
import ru.sergeykarleev.shoppinglist.PrototypeCreating;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MyFragmentDialogTemplates extends DialogFragment implements OnClickListener, PrototypeCreating{
		
	//Список шаблонов
	ListView lvTemplates;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().setTitle("Загрузка из шаблона");
		View v = inflater.inflate(R.layout.dialog_templates, container);
		v.findViewById(R.id.btnImportCancel).setOnClickListener(this);
		v.findViewById(R.id.btnImportLoad).setOnClickListener(this);			
		lvTemplates = (ListView) v.findViewById(R.id.lvTemplates);
		prototypeCreate();				
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
		case R.id.btnImportCancel:
			dismiss();
			break;
		case R.id.btnImportLoad:
			//TODO: передаём имя шаблона для загрузки в список
			Toast.makeText(getActivity(), "здесь будет загрузка выбранного списка", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	@Override
	public void prototypeCreate() {
		String[] prototype_templates = new String[] {"Ежедневный список","Недельный список","Праздничный список","Список в поезд"};		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, prototype_templates);
		lvTemplates.setAdapter(adapter);
	}
}
