/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import OptionComponentsPage from './option.page-object';
import { OptionDeleteDialog } from './option.page-object';
import OptionUpdatePage from './option-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Option e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let optionUpdatePage: OptionUpdatePage;
  let optionComponentsPage: OptionComponentsPage;
  let optionDeleteDialog: OptionDeleteDialog;

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

  it('should load Options', async () => {
    await navBarPage.getEntityPage('option');
    optionComponentsPage = new OptionComponentsPage();
    expect(await optionComponentsPage.getTitle().getText()).to.match(/Options/);
  });

  it('should load create Option page', async () => {
    await optionComponentsPage.clickOnCreateButton();
    optionUpdatePage = new OptionUpdatePage();
    expect(await optionUpdatePage.getPageTitle().getAttribute('id')).to.match(/beautyPointApp.option.home.createOrEditLabel/);
    await optionUpdatePage.cancel();
  });

  it('should create and save Options', async () => {
    async function createOption() {
      await optionComponentsPage.clickOnCreateButton();
      await optionUpdatePage.setNameInput('name');
      expect(await optionUpdatePage.getNameInput()).to.match(/name/);
      await optionUpdatePage.setPriceInput('5');
      expect(await optionUpdatePage.getPriceInput()).to.eq('5');
      await optionUpdatePage.setSessionTimeInput('5');
      expect(await optionUpdatePage.getSessionTimeInput()).to.eq('5');
      const selectedActive = await optionUpdatePage.getActiveInput().isSelected();
      if (selectedActive) {
        await optionUpdatePage.getActiveInput().click();
        expect(await optionUpdatePage.getActiveInput().isSelected()).to.be.false;
      } else {
        await optionUpdatePage.getActiveInput().click();
        expect(await optionUpdatePage.getActiveInput().isSelected()).to.be.true;
      }
      await optionUpdatePage.offerSelectLastOption();
      await waitUntilDisplayed(optionUpdatePage.getSaveButton());
      await optionUpdatePage.save();
      await waitUntilHidden(optionUpdatePage.getSaveButton());
      expect(await optionUpdatePage.getSaveButton().isPresent()).to.be.false;
    }

    await createOption();
    await optionComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeCreate = await optionComponentsPage.countDeleteButtons();
    await createOption();

    await optionComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await optionComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Option', async () => {
    await optionComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await optionComponentsPage.countDeleteButtons();
    await optionComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    optionDeleteDialog = new OptionDeleteDialog();
    expect(await optionDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/beautyPointApp.option.delete.question/);
    await optionDeleteDialog.clickOnConfirmButton();

    await optionComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await optionComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
