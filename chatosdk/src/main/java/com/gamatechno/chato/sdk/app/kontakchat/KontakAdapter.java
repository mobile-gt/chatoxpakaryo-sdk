package com.gamatechno.chato.sdk.app.kontakchat;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gamatechno.chato.sdk.R;
import com.gamatechno.chato.sdk.data.DAO.RoomChat.RoomChat;
import com.gamatechno.chato.sdk.utils.ChatoText.EmphasisTextView.EmphasisTextView;
import com.gamatechno.ggfw_ui.avatarview.AvatarPlaceholder;
import com.gamatechno.ggfw_ui.avatarview.loader.PicassoLoader;
import com.gamatechno.ggfw_ui.avatarview.views.AvatarView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KontakAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    Context context;
    List<KontakModel> kontakModelList;
    OnKontakAdapter onKontakAdapter;

    Boolean hideStatus = false;

    public KontakAdapter(Context context, List<KontakModel> kontakModelList, OnKontakAdapter onKontakAdapter) {
        this.context = context;
        this.kontakModelList = kontakModelList;
        this.onKontakAdapter = onKontakAdapter;
    }

    public KontakAdapter(Context context, List<KontakModel> kontakModelList, boolean hideStatus, OnKontakAdapter onKontakAdapter) {
        this.context = context;
        this.kontakModelList = kontakModelList;
        this.onKontakAdapter = onKontakAdapter;
        this.hideStatus = hideStatus;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View layoutView;
        switch (i){
            case 0:
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_header_kontak, parent, false);
                return new HeaderViewHolder(layoutView);
            case 1:
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_kontak, parent, false);
                return new ViewHolder(layoutView);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final int itemType = getItemViewType(i);
        KontakModel kontakModel = kontakModelList.get(i);
        if(itemType == 0){
            ((HeaderViewHolder)viewHolder).bindHeader(kontakModel);
        } else {
            ((ViewHolder)viewHolder).bindKontak(kontakModel, i);
        }
    }

    @Override
    public int getItemCount() {
        return kontakModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (kontakModelList.get(position).is_header ? 0 : 1);
    }

    public void updateData(List<KontakModel> kontakModels){
        kontakModelList = kontakModels;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        AvatarView avatarView;

        EmphasisTextView tv_name;

        CardView card_indicator;

        TextView tv_position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarView = itemView.findViewById(R.id.avatarView);
            tv_name = itemView.findViewById(R.id.tv_name);
            card_indicator = itemView.findViewById(R.id.card_indicator);
            tv_position = itemView.findViewById(R.id.tv_position);
        }

        private String getRoomName(KontakModel kontakModel){
            return kontakModel.getRoom_type().equals(RoomChat.user_room_type) ? ""+kontakModel.getUser_name() : ""+kontakModel.getGroup_name();
        }

        private void bindKontak(KontakModel kontakModel, int i){
            tv_name.setText(getRoomName(kontakModel));
            if(hideStatus || kontakModel.getRoom_type().equals(RoomChat.group_room_type)){
                card_indicator.setVisibility(View.GONE);
            }

            if(kontakModel.is_group_add){
                bindAddGroup();
            } else {
                bindData(kontakModel, i);
            }
        }

        private void bindData(KontakModel kontakModel, int i){
            PicassoLoader imageLoader = new PicassoLoader();
            AvatarPlaceholder refreshableAvatarPlaceholder = new AvatarPlaceholder(getRoomName(kontakModel));

            imageLoader.loadImage(avatarView, refreshableAvatarPlaceholder, kontakModel.getUser_photo());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onKontakAdapter.onKontakClick(kontakModel, i);
                }
            });

            if(kontakModel.getIs_admin() == 0){
                tv_position.setVisibility(View.GONE);
            } else {
                tv_position.setVisibility(View.VISIBLE);
            }

            if(kontakModel.getIs_online()==1){
                card_indicator.setCardBackgroundColor(context.getResources().getColor(R.color.green_600));
            } else {
                card_indicator.setCardBackgroundColor(context.getResources().getColor(R.color.grey_600));
            }
        }

        private void bindAddGroup(){
//            Picasso.get()
//                    .load(R.drawable.ic_add_group)
//                    .into(avatarView);
            avatarView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_group));

            card_indicator.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onKontakAdapter.onMakeGroup();
                }
            });
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
        }

        private void bindHeader(KontakModel kontakModel){
            tv_name.setText(kontakModel.getUser_name());
        }
    }

    public interface OnKontakAdapter{
        public void onKontakClick(KontakModel kontakModel, int position);
        public void onMakeGroup();
    }
}
