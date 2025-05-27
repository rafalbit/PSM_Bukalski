package wat.psm_lab2_bukalski

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { PersonDB.getPersonDB(androidContext()) }
    single<PersonDao> { get<PersonDB>().personDao() }
    viewModel { PersonViewModel(get()) }
}
