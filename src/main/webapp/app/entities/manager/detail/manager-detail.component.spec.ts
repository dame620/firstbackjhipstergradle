import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ManagerDetailComponent } from './manager-detail.component';

describe('Component Tests', () => {
  describe('Manager Management Detail Component', () => {
    let comp: ManagerDetailComponent;
    let fixture: ComponentFixture<ManagerDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ManagerDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ manager: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ManagerDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ManagerDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load manager on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.manager).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
