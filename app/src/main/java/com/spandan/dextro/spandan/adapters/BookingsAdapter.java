package com.spandan.dextro.spandan.adapters;
import android.view.View;
import android.text.Layout;
import java.util.ArrayList;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.spandan.dextro.spandan.Model.Booking;
import com.spandan.dextro.spandan.Model.BookingViewHolder;
import com.spandan.dextro.spandan.R;


public class BookingsAdapter extends RecyclerView.Adapter<BookingViewHolder> {

    private ArrayList<Booking> bookings;
    private LayoutKind type;
    private int size;


    public enum LayoutKind {
        CurrentBooking,
        PreviousBooking,
        Divider
    }

    public BookingsAdapter(ArrayList<Booking> bookings, LayoutKind type) {

        this.bookings = bookings;
        this.type     = type;
        this.size     = bookings.size();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view               = inflater.inflate(viewType, parent, false);
        switch (viewType) {

            case R.layout.list_item_booking_history:
                return new BookingViewHolder(view, LayoutKind.PreviousBooking);

            case R.layout.list_item_upcoming_booking:
                return new BookingViewHolder(view, LayoutKind.CurrentBooking);

            case R.layout.space_divider: case R.layout.space_divider_horizontally:
                return new BookingViewHolder(view, LayoutKind.Divider);

            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position % 2 == 0) {

            switch (type) {
                case CurrentBooking:
                    return R.layout.list_item_upcoming_booking;
                case PreviousBooking:
                    return R.layout.list_item_booking_history;
                default:
                    throw new IllegalStateException("Booking Type Not Registered : ");
            }
        }
        else {

            switch (type) {

                case CurrentBooking:
                    return R.layout.space_divider_horizontally;
                case PreviousBooking:
                    return R.layout.space_divider;
                default:
                    throw new IllegalStateException("Booking Type Not Registered : ");
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.update(bookings.get(position / 2));
        }
    }

    @Override
    public int getItemCount() { return 2 * size - 1; }
}
