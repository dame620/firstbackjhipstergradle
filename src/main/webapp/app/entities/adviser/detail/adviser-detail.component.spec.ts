import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AdviserDetailComponent } from './adviser-detail.component';

describe('Component Tests', () => {
  describe('Adviser Management Detail Component', () => {
    let comp: AdviserDetailComponent;
    let fixture: ComponentFixture<AdviserDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [AdviserDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ adviser: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(AdviserDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AdviserDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load adviser on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.adviser).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
