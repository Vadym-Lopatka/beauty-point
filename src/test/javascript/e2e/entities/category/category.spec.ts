/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import CategoryComponentsPage from './category.page-object';
import { CategoryDeleteDialog } from './category.page-object';
import CategoryUpdatePage from './category-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Category e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let categoryUpdatePage: CategoryUpdatePage;
  let categoryComponentsPage: CategoryComponentsPage;
  let categoryDeleteDialog: CategoryDeleteDialog;

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

  it('should load Categories', async () => {
    await navBarPage.getEntityPage('category');
    categoryComponentsPage = new CategoryComponentsPage();
    expect(await categoryComponentsPage.getTitle().getText()).to.match(/Categories/);
  });

  it('should load create Category page', async () => {
    await categoryComponentsPage.clickOnCreateButton();
    categoryUpdatePage = new CategoryUpdatePage();
    expect(await categoryUpdatePage.getPageTitle().getAttribute('id')).to.match(/beautyPointApp.category.home.createOrEditLabel/);
    await categoryUpdatePage.cancel();
  });

  it('should create and save Categories', async () => {
    async function createCategory() {
      await categoryComponentsPage.clickOnCreateButton();
      await categoryUpdatePage.setNameInput('name');
      expect(await categoryUpdatePage.getNameInput()).to.match(/name/);
      const selectedMain = await categoryUpdatePage.getMainInput().isSelected();
      if (selectedMain) {
        await categoryUpdatePage.getMainInput().click();
        expect(await categoryUpdatePage.getMainInput().isSelected()).to.be.false;
      } else {
        await categoryUpdatePage.getMainInput().click();
        expect(await categoryUpdatePage.getMainInput().isSelected()).to.be.true;
      }
      await categoryUpdatePage.parentSelectLastOption();
      await waitUntilDisplayed(categoryUpdatePage.getSaveButton());
      await categoryUpdatePage.save();
      await waitUntilHidden(categoryUpdatePage.getSaveButton());
      expect(await categoryUpdatePage.getSaveButton().isPresent()).to.be.false;
    }

    await createCategory();
    await categoryComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeCreate = await categoryComponentsPage.countDeleteButtons();
    await createCategory();

    await categoryComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await categoryComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Category', async () => {
    await categoryComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await categoryComponentsPage.countDeleteButtons();
    await categoryComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    categoryDeleteDialog = new CategoryDeleteDialog();
    expect(await categoryDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/beautyPointApp.category.delete.question/);
    await categoryDeleteDialog.clickOnConfirmButton();

    await categoryComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await categoryComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
