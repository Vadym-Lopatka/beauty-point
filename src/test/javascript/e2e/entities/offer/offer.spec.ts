/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import OfferComponentsPage from './offer.page-object';
import { OfferDeleteDialog } from './offer.page-object';
import OfferUpdatePage from './offer-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Offer e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let offerUpdatePage: OfferUpdatePage;
  let offerComponentsPage: OfferComponentsPage;
  let offerDeleteDialog: OfferDeleteDialog;

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

  it('should load Offers', async () => {
    await navBarPage.getEntityPage('offer');
    offerComponentsPage = new OfferComponentsPage();
    expect(await offerComponentsPage.getTitle().getText()).to.match(/Offers/);
  });

  it('should load create Offer page', async () => {
    await offerComponentsPage.clickOnCreateButton();
    offerUpdatePage = new OfferUpdatePage();
    expect(await offerUpdatePage.getPageTitle().getAttribute('id')).to.match(/beautyPointApp.offer.home.createOrEditLabel/);
    await offerUpdatePage.cancel();
  });

  it('should create and save Offers', async () => {
    async function createOffer() {
      await offerComponentsPage.clickOnCreateButton();
      await offerUpdatePage.setNameInput('name');
      expect(await offerUpdatePage.getNameInput()).to.match(/name/);
      await offerUpdatePage.setDescriptionInput('description');
      expect(await offerUpdatePage.getDescriptionInput()).to.match(/description/);
      await offerUpdatePage.setPriceLowInput('5');
      expect(await offerUpdatePage.getPriceLowInput()).to.eq('5');
      await offerUpdatePage.setPriceHighInput('5');
      expect(await offerUpdatePage.getPriceHighInput()).to.eq('5');
      const selectedActive = await offerUpdatePage.getActiveInput().isSelected();
      if (selectedActive) {
        await offerUpdatePage.getActiveInput().click();
        expect(await offerUpdatePage.getActiveInput().isSelected()).to.be.false;
      } else {
        await offerUpdatePage.getActiveInput().click();
        expect(await offerUpdatePage.getActiveInput().isSelected()).to.be.true;
      }
      await offerUpdatePage.statusSelectLastOption();
      await offerUpdatePage.salonSelectLastOption();
      await offerUpdatePage.imageSelectLastOption();
      await offerUpdatePage.categorySelectLastOption();
      await waitUntilDisplayed(offerUpdatePage.getSaveButton());
      await offerUpdatePage.save();
      await waitUntilHidden(offerUpdatePage.getSaveButton());
      expect(await offerUpdatePage.getSaveButton().isPresent()).to.be.false;
    }

    await createOffer();
    await offerComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeCreate = await offerComponentsPage.countDeleteButtons();
    await createOffer();

    await offerComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await offerComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Offer', async () => {
    await offerComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await offerComponentsPage.countDeleteButtons();
    await offerComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    offerDeleteDialog = new OfferDeleteDialog();
    expect(await offerDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/beautyPointApp.offer.delete.question/);
    await offerDeleteDialog.clickOnConfirmButton();

    await offerComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await offerComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
