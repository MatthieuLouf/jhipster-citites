import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { CityDisplayerSharedModule } from 'app/shared/shared.module';
import { CityDisplayerCoreModule } from 'app/core/core.module';
import { CityDisplayerAppRoutingModule } from './app-routing.module';
import { CityDisplayerHomeModule } from './home/home.module';
import { CityDisplayerEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { JhiMainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    CityDisplayerSharedModule,
    CityDisplayerCoreModule,
    CityDisplayerHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    CityDisplayerEntityModule,
    CityDisplayerAppRoutingModule
  ],
  declarations: [JhiMainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [JhiMainComponent]
})
export class CityDisplayerAppModule {}
