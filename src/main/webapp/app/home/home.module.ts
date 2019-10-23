import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CityDisplayerSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';

@NgModule({
  imports: [CityDisplayerSharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent]
})
export class CityDisplayerHomeModule {}
