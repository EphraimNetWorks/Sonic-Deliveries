package io.acsint.heritageGhana.MtnHeritageGhanaApp.di.modules


import com.example.deliveryapp.ui.login.LoginActivity
import com.example.deliveryapp.ui.main.MainActivity
import com.example.deliveryapp.ui.new_delivery.NewDeliveryActivity
import com.example.deliveryapp.ui.signup.SignUpActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {


    @ContributesAndroidInjector
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    internal abstract fun bindSignUpActivity(): SignUpActivity

    @ContributesAndroidInjector
    internal abstract fun bindNewDeliveryActivity(): NewDeliveryActivity

}
