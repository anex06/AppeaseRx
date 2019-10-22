package com.spandan.dextro.spandan.Model;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.spandan.dextro.spandan.R;
import com.spandan.dextro.spandan.adapters.BookingsAdapter;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookingViewHolder extends RecyclerView.ViewHolder {

    private BookingsAdapter.LayoutKind type;

    private TextView centerNameOutlet;
    private TextView doctorName;
    private TextView address;
    private TextView date;
    /*******/
    private TextView time;

    public BookingViewHolder(@NonNull View itemView, BookingsAdapter.LayoutKind kind) {

        super(itemView);

        type = kind;
        switch (type) {
            case PreviousBooking:
                centerNameOutlet = itemView.findViewById(R.id.tv_center_name);
                doctorName       = itemView.findViewById(R.id.tv_doc_name);
                address          = itemView.findViewById(R.id.tv_center_address);
                date             = itemView.findViewById(R.id.tv_date);
                break;

            case CurrentBooking:
                centerNameOutlet = itemView.findViewById(R.id.tv_center_name);
                doctorName       = itemView.findViewById(R.id.tv_doc_name);
                address          = itemView.findViewById(R.id.tv_center_address);
                date             = itemView.findViewById(R.id.tv_date);
                /**********/
                time             = itemView.findViewById(R.id.tv_consult_time);
                address.setSelected(true);
                break;

            case Divider:
                break;
        }
    }


    public void update(Booking booking) {

        if (type == BookingsAdapter.LayoutKind.Divider) throw new IllegalStateException("Can not update a divider with data.");
        SimpleDateFormat parser = new SimpleDateFormat("d'th of' MMM',' yyyy", Locale.US);
        centerNameOutlet.setText(booking.getCenterName());
        date.setText(parser.format(booking.getDate()));
        doctorName.setText(booking.getDoctorName());
        address.setText(booking.getAddress());
        /*******/
        if (time != null) time.setText(booking.getTimeSlot());
    }
}
