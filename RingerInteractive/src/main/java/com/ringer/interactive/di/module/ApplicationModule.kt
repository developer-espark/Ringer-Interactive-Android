package com.ringer.interactive.di.module

import android.app.KeyguardManager
import android.content.ClipboardManager
import android.content.Context
import android.media.AudioManager
import android.os.PowerManager
import android.os.Vibrator
import android.telecom.TelecomManager
import android.telephony.SubscriptionManager
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationManagerCompat
import com.ringer.interactive.di.factory.contentresolver.ContentResolverFactory
import com.ringer.interactive.di.factory.contentresolver.ContentResolverFactoryImpl
import com.ringer.interactive.di.factory.fragment.FragmentFactory
import com.ringer.interactive.di.factory.fragment.FragmentFactoryImpl
import com.ringer.interactive.di.factory.livedata.LiveDataFactory
import com.ringer.interactive.di.factory.livedata.LiveDataFactoryImpl
import com.ringer.interactive.interactor.animation.AnimationsInteractor
import com.ringer.interactive.interactor.animation.AnimationsInteractorImpl
import com.ringer.interactive.interactor.audio.AudiosInteractor
import com.ringer.interactive.interactor.audio.AudiosInteractorImpl
import com.ringer.interactive.interactor.blocked.BlockedInteractor
import com.ringer.interactive.interactor.blocked.BlockedInteractorImpl
import com.ringer.interactive.interactor.callaudio.CallAudiosInteractor
import com.ringer.interactive.interactor.callaudio.CallAudiosInteractorImpl
import com.ringer.interactive.interactor.calls.CallsInteractor
import com.ringer.interactive.interactor.calls.CallsInteractorImpl
import com.ringer.interactive.interactor.color.ColorsInteractor
import com.ringer.interactive.interactor.color.ColorsInteractorImpl
import com.ringer.interactive.interactor.contacts.ContactsInteractor
import com.ringer.interactive.interactor.contacts.ContactsInteractorImpl
import com.ringer.interactive.interactor.drawable.DrawablesInteractor
import com.ringer.interactive.interactor.drawable.DrawablesInteractorImpl
import com.ringer.interactive.interactor.navigation.NavigationsInteractor
import com.ringer.interactive.interactor.navigation.NavigationsInteractorImpl
import com.ringer.interactive.interactor.permission.PermissionsInteractor
import com.ringer.interactive.interactor.permission.PermissionsInteractorImpl
import com.ringer.interactive.interactor.phoneaccounts.PhonesInteractor
import com.ringer.interactive.interactor.phoneaccounts.PhonesInteractorImpl
import com.ringer.interactive.interactor.preferences.PreferencesInteractor
import com.ringer.interactive.interactor.preferences.PreferencesInteractorImpl
import com.ringer.interactive.interactor.proximity.ProximitiesInteractor
import com.ringer.interactive.interactor.proximity.ProximitiesInteractorImpl
import com.ringer.interactive.interactor.recents.RecentsInteractor
import com.ringer.interactive.interactor.recents.RecentsInteractorImpl
import com.ringer.interactive.interactor.sim.SimsInteractor
import com.ringer.interactive.interactor.sim.SimsInteractorImpl
import com.ringer.interactive.interactor.string.StringsInteractor
import com.ringer.interactive.interactor.string.StringsInteractorImpl
import com.ringer.interactive.repository.calls.CallsRepository
import com.ringer.interactive.repository.calls.CallsRepositoryImpl
import com.ringer.interactive.repository.contacts.ContactsRepository
import com.ringer.interactive.repository.contacts.ContactsRepositoryImpl
import com.ringer.interactive.repository.phones.PhonesRepository
import com.ringer.interactive.repository.phones.PhonesRepositoryImpl
import com.ringer.interactive.repository.recents.RecentsRepository
import com.ringer.interactive.repository.recents.RecentsRepositoryImpl
import com.ringer.interactive.util.PreferencesManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.disposables.CompositeDisposable

@InstallIn(SingletonComponent::class)
@Module(includes = [ApplicationModule.BindsModule::class])
class ApplicationModule {
    @Provides
    fun provideDisposables(): CompositeDisposable = CompositeDisposable()

    @Provides
    fun provideVibrator(@ApplicationContext context: Context): Vibrator =
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    //region manager

    @Provides
    fun providePowerManager(@ApplicationContext context: Context): PowerManager =
        context.getSystemService(Context.POWER_SERVICE) as PowerManager

    @Provides
    fun provideAudioManager(@ApplicationContext context: Context): AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    @Provides
    fun provideTelecomManager(@ApplicationContext context: Context): TelecomManager =
        context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    @Provides
    fun provideKeyguardManager(@ApplicationContext context: Context): KeyguardManager =
        context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    @Provides
    fun provideClipboardManager(@ApplicationContext context: Context): ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    @Provides
    fun provideInputMethodManager(@ApplicationContext context: Context): InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    @Provides
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager =
        PreferencesManager.getInstance(context)

    @Provides
    fun provideSubscriptionManager(@ApplicationContext context: Context): SubscriptionManager =
        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    //endregion

    @Module
    @InstallIn(SingletonComponent::class)
    interface BindsModule {
        //region repository

        @Binds
        fun bindCallsRepository(callsRepositoryImpl: CallsRepositoryImpl): CallsRepository

        @Binds
        fun bindPhonesRepository(phonesRepositoryImpl: PhonesRepositoryImpl): PhonesRepository

        @Binds
        fun bindRecentsRepository(recentsRepositoryImpl: RecentsRepositoryImpl): RecentsRepository

        @Binds
        fun bindContactsRepository(contactsRepositoryImpl: ContactsRepositoryImpl): ContactsRepository

        //endregion

        //region factory

        @Binds
        fun bindLiveDataFactory(liveDataFactoryImpl: LiveDataFactoryImpl): LiveDataFactory

        @Binds
        fun bindFragmentFactory(fragmentFactoryImpl: FragmentFactoryImpl): FragmentFactory

        @Binds
        fun bindContentResolverFactory(contentResolverFactoryImpl: ContentResolverFactoryImpl): ContentResolverFactory

        //endregion

        //region interactor

        @Binds
        fun bindSimsInteractor(simsInteractorImpl: SimsInteractorImpl): SimsInteractor

        @Binds
        fun bindCallsInteractor(callsInteractorImpl: CallsInteractorImpl): CallsInteractor

        @Binds
        fun bindColorsInteractor(colorsInteractorImpl: ColorsInteractorImpl): ColorsInteractor

        @Binds
        fun bindAudiosInteractor(audiosInteractorImpl: AudiosInteractorImpl): AudiosInteractor

        @Binds
        fun bindPhonesInteractor(phonesInteractorImpl: PhonesInteractorImpl): PhonesInteractor

        @Binds
        fun bindStringsInteractor(stringsInteractorImpl: StringsInteractorImpl): StringsInteractor

        @Binds
        fun bindBlockedInteractor(blockedInteractorImpl: BlockedInteractorImpl): BlockedInteractor

        @Binds
        fun bindRecentsInteractor(recentsInteractorImpl: RecentsInteractorImpl): RecentsInteractor

        @Binds
        fun bindContactsInteractor(contactsInteractorImpl: ContactsInteractorImpl): ContactsInteractor

        @Binds
        fun bindDrawablesInteractor(drawablesInteractorImpl: DrawablesInteractorImpl): DrawablesInteractor

        @Binds
        fun bindAnimationsInteractor(animationsInteractorImpl: AnimationsInteractorImpl): AnimationsInteractor

        @Binds
        fun bindCallAudiosInteractor(callAudiosInteractorImpl: CallAudiosInteractorImpl): CallAudiosInteractor

        @Binds
        fun bindProximitiesInteractor(proximitiesInteractorImpl: ProximitiesInteractorImpl): ProximitiesInteractor

        @Binds
        fun bindPermissionsInteractor(permissionsInteractorImpl: PermissionsInteractorImpl): PermissionsInteractor

        @Binds
        fun bindPreferencesInteractor(preferencesInteractorImpl: PreferencesInteractorImpl): PreferencesInteractor

        @Binds
        fun bindNavigationsInteractor(navigationsInteractorImpl: NavigationsInteractorImpl): NavigationsInteractor

        //endregion
    }
}