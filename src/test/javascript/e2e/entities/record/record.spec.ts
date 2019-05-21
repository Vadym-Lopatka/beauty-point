/* tslint:disable no-unused-expression */
import { browser, element, by, protractor } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import RecordComponentsPage from './record.page-object';
import { RecordDeleteDialog } from './record.page-object';
import RecordUpdatePage from './record-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Record e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let recordUpdatePage: RecordUpdatePage;
  let recordComponentsPage: RecordComponentsPage;
  /*let recordDeleteDialog: RecordDeleteDialog;*/

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

  it('should load Records', async () => {
    await navBarPage.getEntityPage('record');
    recordComponentsPage = new RecordComponentsPage();
    expect(await recordComponentsPage.getTitle().getText()).to.match(/Records/);
  });

  it('should load create Record page', async () => {
    await recordComponentsPage.clickOnCreateButton();
    recordUpdatePage = new RecordUpdatePage();
    expect(await recordUpdatePage.getPageTitle().getAttribute('id')).to.match(/beautyPointApp.record.home.createOrEditLabel/);
    await recordUpdatePage.cancel();
  });

  /* it('should create and save Records', async () => {
        async function createRecord() {
            await recordComponentsPage.clickOnCreateButton();
            await recordUpdatePage.setBookingTimeInput('01/01/2001' + protractor.Key.TAB + '02:30AM');
            expect(await recordUpdatePage.getBookingTimeInput()).to.contain('2001-01-01T02:30');
            await recordUpdatePage.setDurationInput('5');
            expect(await recordUpdatePage.getDurationInput()).to.eq('5');
            await recordUpdatePage.setTotalPriceInput('5');
            expect(await recordUpdatePage.getTotalPriceInput()).to.eq('5');
            await recordUpdatePage.orderStatusSelectLastOption();
            await recordUpdatePage.setCommentInput('comment');
            expect(await recordUpdatePage.getCommentInput()).to.match(/comment/);
            await recordUpdatePage.masterSelectLastOption();
            await recordUpdatePage.variantSelectLastOption();
            // recordUpdatePage.optionSelectLastOption();
            await recordUpdatePage.userSelectLastOption();
            await recordUpdatePage.salonSelectLastOption();
            await waitUntilDisplayed(recordUpdatePage.getSaveButton());
            await recordUpdatePage.save();
            await waitUntilHidden(recordUpdatePage.getSaveButton());
            expect(await recordUpdatePage.getSaveButton().isPresent()).to.be.false;
        }

        await createRecord();
        await recordComponentsPage.waitUntilLoaded();
        const nbButtonsBeforeCreate = await recordComponentsPage.countDeleteButtons();
        await createRecord();

        await recordComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
        expect(await recordComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    });*/

  /* it('should delete last Record', async () => {
        await recordComponentsPage.waitUntilLoaded();
        const nbButtonsBeforeDelete = await recordComponentsPage.countDeleteButtons();
        await recordComponentsPage.clickOnLastDeleteButton();

        const deleteModal = element(by.className('modal'));
        await waitUntilDisplayed(deleteModal);

        recordDeleteDialog = new RecordDeleteDialog();
        expect(await recordDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/beautyPointApp.record.delete.question/);
        await recordDeleteDialog.clickOnConfirmButton();

        await recordComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
        expect(await recordComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    });*/

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
