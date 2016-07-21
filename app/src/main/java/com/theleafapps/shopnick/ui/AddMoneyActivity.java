package com.theleafapps.shopnick.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.theleafapps.shopnick.R;
import com.theleafapps.shopnick.dialogs.MoneyAddedDialog;
import com.theleafapps.shopnick.models.Customer;
import com.theleafapps.shopnick.models.multiples.Customers;
import com.theleafapps.shopnick.tasks.GetCustomerByIdTask;
import com.theleafapps.shopnick.tasks.UpdateCustomerWalletValueTask;
import com.theleafapps.shopnick.utils.Communicator;

import java.util.concurrent.ExecutionException;

public class AddMoneyActivity extends AppCompatActivity implements Communicator{

    Toolbar toolbar;
    Customer customer;
    ActionBar actionBar;
    Button add_wallet_money_button;
    MoneyAddedDialog moneyAddedDialog;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        toolbar         =   (Toolbar) findViewById(R.id.toolbar_addmoney);
        setSupportActionBar(toolbar);

        actionBar       =   getSupportActionBar();
        actionBar.setTitle("Add Money");

        add_wallet_money_button =   (Button) findViewById(R.id.add_wallet_money_button);
        moneyAddedDialog        =   new MoneyAddedDialog();
        fragmentManager         =   getFragmentManager();

        add_wallet_money_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent       =   getIntent();
                    int customer_id     =   intent.getIntExtra("customer_id", 0);
                    double cart_total   =   intent.getFloatExtra("cart_total", 0);

                    if (customer_id != 0) {
                        GetCustomerByIdTask getCustomerByIdTask
                                                =   new GetCustomerByIdTask(AddMoneyActivity.this, customer_id);
                        getCustomerByIdTask.execute().get();

                        customer = getCustomerByIdTask.customerRec;

                        if(customer!=null) {
                            customer.wallet_value   =   10000.0;
                            Customers customersObj  =   new Customers();
                            customersObj.customers.add(customer);

                            UpdateCustomerWalletValueTask updateCustomerWalletValueTask
                                    = new UpdateCustomerWalletValueTask(AddMoneyActivity.this, customersObj);
                            updateCustomerWalletValueTask.execute().get();

                            Bundle arguments = new Bundle();
                            arguments.putInt("cart_total",(int)cart_total);
                            moneyAddedDialog.setArguments(arguments);
                            moneyAddedDialog.show(fragmentManager,"Money_Added");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void dialogMessage(String msg) {

    }
}