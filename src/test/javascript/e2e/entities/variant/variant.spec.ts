/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import VariantComponentsPage from './variant.page-object';
import { VariantDeleteDialog } from './variant.page-object';
import VariantUpdatePage from './variant-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Variant e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let variantUpdatePage: VariantUpdatePage;
  let variantComponentsPage: VariantComponentsPage;
  let variantDeleteDialog: VariantDeleteDialog;

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

  it('should load Variants', async () => {
    await navBarPage.getEntityPage('variant');
    variantComponentsPage = new VariantComponentsPage();
    expect(await variantComponentsPage.getTitle().getText()).to.match(/Variants/);
  });

  it('should load create Variant page', async () => {
    await variantComponentsPage.clickOnCreateButton();
    variantUpdatePage = new VariantUpdatePage();
    expect(await variantUpdatePage.getPageTitle().getAttribute('id')).to.match(/beautyPointApp.variant.home.createOrEditLabel/);
    await variantUpdatePage.cancel();
  });

  it('should create and save Variants', async () => {
    async function createVariant() {
      await variantComponentsPage.clickOnCreateButton();
      await variantUpdatePage.setNameInput('name');
      expect(await variantUpdatePage.getNameInput()).to.match(/name/);
      await variantUpdatePage.setPriceInput('5');
      expect(await variantUpdatePage.getPriceInput()).to.eq('5');
      await variantUpdatePage.setSessionTimeInput('5');
      expect(await variantUpdatePage.getSessionTimeInput()).to.eq('5');
      const selectedActive = await variantUpdatePage.getActiveInput().isSelected();
      if (selectedActive) {
        await variantUpdatePage.getActiveInput().click();
        expect(await variantUpdatePage.getActiveInput().isSelected()).to.be.false;
      } else {
        await variantUpdatePage.getActiveInput().click();
        expect(await variantUpdatePage.getActiveInput().isSelected()).to.be.true;
      }
      await variantUpdatePage.offerSelectLastOption();
      // variantUpdatePage.executorSelectLastOption();
      await waitUntilDisplayed(variantUpdatePage.getSaveButton());
      await variantUpdatePage.save();
      await waitUntilHidden(variantUpdatePage.getSaveButton());
      expect(await variantUpdatePage.getSaveButton().isPresent()).to.be.false;
    }

    await createVariant();
    await variantComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeCreate = await variantComponentsPage.countDeleteButtons();
    await createVariant();

    await variantComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await variantComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Variant', async () => {
    await variantComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await variantComponentsPage.countDeleteButtons();
    await variantComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    variantDeleteDialog = new VariantDeleteDialog();
    expect(await variantDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/beautyPointApp.variant.delete.question/);
    await variantDeleteDialog.clickOnConfirmButton();

    await variantComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await variantComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
