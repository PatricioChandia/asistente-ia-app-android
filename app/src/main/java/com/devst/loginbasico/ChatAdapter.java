package com.devst.loginbasico;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.loginbasico.network.MessageModel;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Constantes para los tipos de vista
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_ASSISTANT = 2;

    private List<MessageModel> messages = new ArrayList<>();

    // 1. Clase ViewHolder para el USUARIO
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }

    // 2. Clase ViewHolder para el ASISTENTE
    public static class AssistantViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        public AssistantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }

    // 3. Método para decidir qué layout usar
    @Override
    public int getItemViewType(int position) {
        MessageModel message = messages.get(position);
        if ("user".equals(message.getRole())) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_ASSISTANT;
        }
    }

    // 4. Método que "infla" (crea) el layout correcto
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_assistant, parent, false);
            return new AssistantViewHolder(view);
        }
    }

    // 5. Método que pone los datos (el texto) en el layout
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_USER) {
            ((UserViewHolder) holder).tvMessage.setText(message.getContent());
        } else {
            ((AssistantViewHolder) holder).tvMessage.setText(message.getContent());
        }
    }

    // 6. Método que le dice al RecyclerView cuántos items hay
    @Override
    public int getItemCount() {
        return messages.size();
    }

    // --- Métodos públicos para actualizar la lista ---

    // Reemplaza toda la lista (para cargar el historial)
    public void setMessages(List<MessageModel> messageList) {
        this.messages = messageList;
        notifyDataSetChanged(); // Refresca toda la lista
    }

    // Añade un solo mensaje al final (para chats nuevos)
    public void addMessage(MessageModel message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1); // Refresca solo el último item
    }
}