package com.ractivedev.passwordmaster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class ActivityMainFragment extends ListFragment {

    static LoginListAdapter loginAdapter;
    EditText filterEditText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginAdapter = new LoginListAdapter(LoginList.getLoginList(), getActivity());
        setListAdapter(loginAdapter);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        filterEditText = getActivity().findViewById(R.id.filterEditText);
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0) {
                    loginAdapter.getFilter().filter(charSequence.toString());
                } else {
                    loginAdapter.getFilter().filter("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateContent();
        Stuff.setEditTextSize(filterEditText);
    }

    public static void updateContent() {
        LoginList.sortLoginArrayList();
        LoginList.saveLogins();
        loginAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        LoginList.saveLogins();
        super.onPause();
    }

    public class LoginListAdapter extends BaseAdapter implements Filterable{

        LayoutInflater inflater;
        ArrayList<Login> loginArrayList, filteredLoginArrayList;

        public LoginListAdapter(ArrayList<Login> loginList, Context context) {
            inflater = LayoutInflater.from(context);
            loginArrayList = loginList;
            filteredLoginArrayList = loginList;
        }

        @Override
        public int getCount() {
            return filteredLoginArrayList.size();
        }

        @Override
        public Login getItem(int i) {
            return filteredLoginArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.view_login_in_fragment_list, null);
            setupLogin(getItem(i), view);
            return view;
        }

        private void setupLogin(final Login login, View view) {
            TextView loginTitle = view.findViewById(R.id.loginTitleTextView);
            TextView loginContent = view.findViewById(R.id.loginContentTextView);

            Stuff.setTextViewSize(loginTitle, loginContent);

            ImageView imageView = view.findViewById(R.id.loginImageView);
            final CheckBox favCheckBox = view.findViewById(R.id.favCheckBox);

            loginTitle.setText(login.getTitle());
            if(Settings.isDarkThemeEnabled()) {
                loginTitle.setTextColor(getResources().getColor(R.color.dark_theme_text_color));
            }

            loginContent.setText(login.getUsername());
            loginContent.setTextColor(getResources().getColor(R.color.dark_theme_button_color));

            imageView.setImageBitmap(login.getImage(getContext()));
            favCheckBox.setChecked(login.getFavorite());
            favCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    login.setFavorite(favCheckBox.isChecked());
                    if(login.getFavorite()){
                        Stuff.showToast(login.getTitle() + " " + getString(R.string.login_favorite_action),getContext());
                    } else {
                        Stuff.showToast(login.getTitle() + " " + getString(R.string.login_unfavorite_action),getContext());
                    }
                    notifyDataSetChanged();
                }
            });
            Stuff.setCheckBoxTextSize(favCheckBox);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openLogin(login);
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openLogin(login);
                }
            });
            loginTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openLogin(login);
                }
            });
            loginContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openLogin(login);
                }
            });
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    ArrayList<Login> FilteredArrList = new ArrayList<>();

                    if (constraint == null || constraint.length() == 0) {
                        results.count = loginArrayList.size();
                        results.values = loginArrayList;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (Login login : loginArrayList) {
                            if (login.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || login.getWebsite().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || login.getUsername().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                FilteredArrList.add(login);
                            }
                        }
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filteredLoginArrayList = (ArrayList<Login>) filterResults.values;
                    loginAdapter.notifyDataSetChanged();
                }
            };
            return filter;
        }
    }

    private void openLogin(Login login) {
        Intent newIntent = new Intent(getActivity(), ActivityShowLogin.class);
        newIntent.putExtra(Stuff.LOGIN_ID, login.getId());
        startActivity(newIntent);
    }
}
