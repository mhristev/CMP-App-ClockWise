package com.clockwise.core.di

import com.clockwise.features.business.presentation.BusinessViewModel
import com.clockwise.features.business.presentation.add_employee.SearchViewModel
import com.clockwise.features.availability.presentation.calendar.CalendarViewModel
import com.clockwise.features.profile.presentation.ProfileViewModel
import com.clockwise.features.shift.presentation.week_schedule.WeeklyScheduleViewModel
import com.clockwise.features.shift.presentation.welcome_shifts.WelcomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for providing ViewModels.
 */
val viewModelModule = module {
    viewModel { WelcomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { WeeklyScheduleViewModel(get()) }
    viewModel { CalendarViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { BusinessViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
} 