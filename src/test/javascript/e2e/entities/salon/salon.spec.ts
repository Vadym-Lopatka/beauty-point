/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import SalonComponentsPage from './salon.page-object';
import { SalonDeleteDialog } from './salon.page-object';
import SalonUpdatePage from './salon-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Salon e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let salonUpdatePage: SalonUpdatePage;
  let salonComponentsPage: SalonComponentsPage;
  /*let salonDeleteDialog: SalonDeleteDialog;*/

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();
    await waitUntilDisplayed(navBarPage.entityMenu);
  });

  it('should load Salons', async () => {
    await navBarPage.getEntityPage('salon');
    salonComponentsPage = new SalonComponentsPage();
    expect(await salonComponentsPage.getTitle().getText()).to.match(/Salons/);
  });

  it('should load create Salon page', async () => {
    await salonComponentsPage.clickOnCreateButton();
    salonUpdatePage = new SalonUpdatePage();
    expect(await salonUpdatePage.getPageTitle().getAttribute('id')).to.match(/beautyPointApp.salon.home.createOrEditLabel/);
    await salonUpdatePage.cancel();
  });

  /* it('should create and save Salons', async () => {
        async function createSalon() {
            await salonComponentsPage.clickOnCreateButton();
            await salonUpdatePage.setNameInput('name');
            expect(await salonUpdatePage.getNameInput()).to.match(/name/);
            await salonUpdatePage.setSloganInput('slogan');
            expect(await salonUpdatePage.getSloganInput()).to.match(/slogan/);
            await salonUpdatePage.setLocationInput('location');
            expect(await salonUpdatePage.getLocationInput()).to.match(/location/);
            await salonUpdatePage.statusSelectLastOption();
            await salonUpdatePage.setSystemCommentInput('systemComment');
            expect(await salonUpdatePage.getSystemCommentInput()).to.match(/systemComment/);
            await salonUpdatePage.typeSelectLastOption();
            await salonUpdatePage.imageSelectLastOption();
            await salonUpdatePage.timeTableSelectLastOption();
            // salonUpdatePage.categorySelectLastOption();
            await salonUpdatePage.ownerSelectLastOption();
            await waitUntilDisplayed(salonUpdatePage.getSaveButton());
            await salonUpdatePage.save();
            await waitUntilHidden(salonUpdatePage.getSaveButton());
            expect(await salonUpdatePage.getSaveButton().isPresent()).to.be.false;
        }

        await createSalon();
        await salonComponentsPage.waitUntilLoaded();
        const nbButtonsBeforeCreate = await salonComponentsPage.countDeleteButtons();
        await createSalon();

        await salonComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
        expect(await salonComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    });*/

  /* it('should delete last Salon', async () => {
        await salonComponentsPage.waitUntilLoaded();
        const nbButtonsBeforeDelete = await salonComponentsPage.countDeleteButtons();
        await salonComponentsPage.clickOnLastDeleteButton();

        const deleteModal = element(by.className('modal'));
        await waitUntilDisplayed(deleteModal);

        salonDeleteDialog = new SalonDeleteDialog();
        expect(await salonDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/beautyPointApp.salon.delete.question/);
        await salonDeleteDialog.clickOnConfirmButton();

        await salonComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
        expect(await salonComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    });*/

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
