package com.zio.docsguard;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

public class AdapterFiles extends RecyclerView.Adapter<AdapterFiles.ViewHolder> {

    Context context;
    List<ModelFiles> FilesList;

    public AdapterFiles(Context context, List<ModelFiles> filesList) {
        this.context = context;
        FilesList = filesList;
    }

    @NonNull
    @Override
    public AdapterFiles.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_file, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFiles.ViewHolder holder, int position) {
        ModelFiles model = FilesList.get(position);
        holder.title.setText(model.getName());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetails();
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String FileName = model.getName() + ".pdf";
                File requestFile = new File(context.getFilesDir(), FileName);

                //Most file-related method calls need to be in

                Uri fileUri = null;
                try {
                    fileUri = FileProvider.getUriForFile(
                            context,
                            context.getPackageName() + ".fileprovider",
                            requestFile);
                } catch (IllegalArgumentException e) {
                    Log.e("File Selector",
                            e.getMessage());
                }

                if (fileUri != null) {

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    sendIntent.setDataAndType(
                            fileUri,
                            context.getContentResolver().getType(fileUri));
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

// Try to invoke the intent.
                    try {
                        context.startActivity(Intent.createChooser(sendIntent, "Share"));
                        //context.startActivity(sendIntent);
                    } catch (ActivityNotFoundException e) {
                        // Define what your app should do if no activity can handle the intent.
                    }
                } else {
                    FilesList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, FilesList.size());
                    Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openDetails() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.popup_details);

        //LinearLayout copy = bottomSheetDialog.findViewById(R.id.copyLinearLayout);


        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return FilesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageButton share;
        public CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            share = itemView.findViewById(R.id.sharebtn);
            card = itemView.findViewById(R.id.card);
        }
    }
}
