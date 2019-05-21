/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import MasterComponentsPage from './master.page-object';
import { MasterDeleteDialog } from './master.page-object';
import MasterUpdatePage from './master-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Master e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let masterUpdatePage: MasterUpdatePage;
  let masterComponentsPage: MasterComponentsPage;
  /*let masterDeleteDialog: MasterDeleteDialog;*/

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

  it('should load Masters', async () => {
    await navBarPage.getEntityPage('master');
    masterComponentsPage = new MasterComponentsPage();
    expect(await masterComponentsPage.getTitle().getText()).to.match(/Masters/);
  });

  it('should load create Master page', async () => {
    await masterComponentsPage.clickOnCreateButton();
    masterUpdatePage = new MasterUpdatePage();
    expect(await masterUpdatePage.getPageTitle().getAttribute('id')).to.match(/beautyPointApp.master.home.createOrEditLabel/);
    await masterUpdatePage.cancel();
  });

  /* it('should create and save Masters', async () => {
        async function createMaster() {
            await masterComponentsPage.clickOnCreateButton();
            await masterUpdatePage.salonSelectLastOption();
            // masterUpdatePage.categorySelectLastOption();
            await masterUpdatePage.userSelectLastOption();
            await waitUntilDisplayed(masterUpdatePage.getSaveButton());
            await masterUpdatePage.save();
            await waitUntilHidden(masterUpdatePage.getSaveButton());
            expect(await masterUpdatePage.getSaveButton().isPresent()).to.be.false;
        }

        await createMaster();
        await masterComponentsPage.waitUntilLoaded();
        const nbButtonsBeforeCreate = await masterComponentsPage.countDeleteButtons();
        await createMaster();

        await masterComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
        expect(await masterComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    });*/

  /* it('should delete last Master', async () => {
        await masterComponentsPage.waitUntilLoaded();
        const nbButtonsBeforeDelete = await masterComponentsPage.countDeleteButtons();
        await masterComponentsPage.clickOnLastDeleteButton();

        const deleteModal = element(by.className('modal'));
        await waitUntilDisplayed(deleteModal);

        masterDeleteDialog = new MasterDeleteDialog();
        expect(await masterDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/beautyPointApp.master.delete.question/);
        await masterDeleteDialog.clickOnConfirmButton();

        await masterComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
        expect(await masterComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    });*/

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
