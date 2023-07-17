package sa.paylink.sdk.android.paylinkgatewaysdktester;

public interface Callback<T, E> {
    void onSuccess(T response);

    void onError(E error);
}