package sa.paylink.sdk.android.paylinkgatewaysdktester;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import sa.paylink.sdk.android.paylinkgatewaysdktester.databinding.FragmentFirstBinding;
import sa.paylink.sdk.android.plpaymentgateway.APIError;
import sa.paylink.sdk.android.plpaymentgateway.Callback;
import sa.paylink.sdk.android.plpaymentgateway.Environment;
import sa.paylink.sdk.android.plpaymentgateway.PaylinkGateway;
import sa.paylink.sdk.android.plpaymentgateway.model.PLOrder;
import sa.paylink.sdk.android.plpaymentgateway.model.PLPaylinkCallbackData;
import sa.paylink.sdk.android.plpaymentgateway.model.PLProduct;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private PaylinkGateway paylinkGateway;
    private final String APP_ID = "APP_ID_1123453311";
    private final String SECRET_KEY = "0662abb5-13c7-38ab-cd12-236e58f43766";
    private final Context context;

    public FirstFragment() {
        this.context = this.getContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.paylinkGateway = new PaylinkGateway(APP_ID, SECRET_KEY, Environment.TEST, this.getContext());
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void processPayment() {
        this.paylinkGateway.auth(new Callback<String, APIError>() {
            @Override
            public void onSuccess(String token) {
                System.out.println("token is: " + token);

                List<PLProduct> products = new ArrayList<>();

                products.add(new PLProduct("Product 1", 25, 1));
                products.add(new PLProduct("Product 2", 25, 1));
                products.add(new PLProduct("Product 3", 25, 2));

                paylinkGateway.addInvoice(token,
                        "Zaid Matooq",
                        "0509200900",
                        100,
                        "MERCHANT_ORDER_NUMBER",
                        products,
                        new Callback<PLOrder, APIError>() {
                            @Override
                            public void onSuccess(PLOrder order) {
                                System.out.println("order is: " + order.toString());

                                paylinkGateway.openInvoiceURL(getContext(), order.getMobileUrl(), order.getGatewayOrderRequest().getClientName(), order.getGatewayOrderRequest().getClientMobile(),
                                        new Callback<PLPaylinkCallbackData, APIError>() {
                                            @Override
                                            public void onSuccess(PLPaylinkCallbackData response) {
                                                System.out.println("response is: " + response);
                                            }

                                            @Override
                                            public void onError(APIError error) {
                                                System.out.println("Error is: " + error);
                                            }
                                        }
                                );
                            }

                            @Override
                            public void onError(APIError error) {
                                System.out.println("error is: " + error);
                            }
                        }
                );
            }

            @Override
            public void onError(APIError error) {
                System.out.println("API Error: " + error);
            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonFirst.setOnClickListener(view1 -> {
            processPayment();
            System.out.println("view is: " + view1);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}