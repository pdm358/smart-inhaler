package com.ybeltagy.breathe.collection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.ybeltagy.breathe.Finals;
import com.ybeltagy.breathe.data.DataUtilities;
import com.ybeltagy.breathe.data.InhalerUsageEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Export {

    //todo: add comments to this file.
    // todo: consider making asynchronous
    // todo: move inside another folder



    private static final String tag = Export.class.getName();

    //todo: delete:
    //

    public static Intent extractAllIUE(Context context, List<InhalerUsageEvent> IUEList){

        Uri path = generateCSVFile(context, IUEList);

        if(path == null) return null;

        Intent fileIntent = new Intent(Intent.ACTION_SEND); // todo: consider using view but with the correct mime type.
        fileIntent.setType("text/csv");
        fileIntent.putExtra(Intent.EXTRA_SUBJECT,"IUE Data");
        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        fileIntent.putExtra(Intent.EXTRA_STREAM,path);
        context.startActivity(Intent.createChooser(fileIntent, "Export IUE"));

        return fileIntent;
    }

    private static Uri generateCSVFile(Context context, List<InhalerUsageEvent> IUEList){
        StringBuilder sb = new StringBuilder();

        DataUtilities.addIUETableColumnNames(sb);

        for(InhalerUsageEvent iue : IUEList){
            DataUtilities.appendIUE(sb,iue);
        }

        try(FileOutputStream out = context.openFileOutput(Finals.IUE_DATA_FILE_NAME, Context.MODE_PRIVATE)){
            out.write(sb.toString().getBytes());
        }catch (Exception e){
            // Error saving file
            Log.d(tag,e.toString());
            return null;
        }


        File file = new File(context.getFilesDir(), Finals.IUE_DATA_FILE_NAME);
        Uri path = FileProvider.getUriForFile(context, Finals.FILE_PROVIDER_AUTHORITY_STRING, file); // getUriForFile(context, "${context.packageName}.fileprovider", file)
        return path;
    }

}
