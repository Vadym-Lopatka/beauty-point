/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import TimeTableComponentsPage from './time-table.page-object';
import { TimeTableDeleteDialog } from './time-table.page-object';
import TimeTableUpdatePage from './time-table-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('TimeTable e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let timeTableUpdatePage: TimeTableUpdatePage;
  let timeTableComponentsPage: TimeTableComponentsPage;
  let timeTableDeleteDialog: TimeTableDeleteDialog;

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

  it('should load TimeTables', async () => {
    await navBarPage.getEntityPage('time-table');
    timeTableComponentsPage = new TimeTableComponentsPage();
    expect(await timeTableComponentsPage.getTitle().getText()).to.match(/Time Tables/);
  });

  it('should load create TimeTable page', async () => {
    await timeTableComponentsPage.clickOnCreateButton();
    timeTableUpdatePage = new TimeTableUpdatePage();
    expect(await timeTableUpdatePage.getPageTitle().getAttribute('id')).to.match(/beautyPointApp.timeTable.home.createOrEditLabel/);
    await timeTableUpdatePage.cancel();
  });

  it('should create and save TimeTables', async () => {
    async function createTimeTable() {
      await timeTableComponentsPage.clickOnCreateButton();
      const selectedEveryDayEqual = await timeTableUpdatePage.getEveryDayEqualInput().isSelected();
      if (selectedEveryDayEqual) {
        await timeTableUpdatePage.getEveryDayEqualInput().click();
        expect(await timeTableUpdatePage.getEveryDayEqualInput().isSelected()).to.be.false;
      } else {
        await timeTableUpdatePage.getEveryDayEqualInput().click();
        expect(await timeTableUpdatePage.getEveryDayEqualInput().isSelected()).to.be.true;
      }
      await timeTableUpdatePage.setMoInput('5');
      expect(await timeTableUpdatePage.getMoInput()).to.eq('5');
      await timeTableUpdatePage.setTuInput('5');
      expect(await timeTableUpdatePage.getTuInput()).to.eq('5');
      await timeTableUpdatePage.setWeInput('5');
      expect(await timeTableUpdatePage.getWeInput()).to.eq('5');
      await timeTableUpdatePage.setThInput('5');
      expect(await timeTableUpdatePage.getThInput()).to.eq('5');
      await timeTableUpdatePage.setFrInput('5');
      expect(await timeTableUpdatePage.getFrInput()).to.eq('5');
      await timeTableUpdatePage.setSaInput('5');
      expect(await timeTableUpdatePage.getSaInput()).to.eq('5');
      await timeTableUpdatePage.setSuInput('5');
      expect(await timeTableUpdatePage.getSuInput()).to.eq('5');
      await waitUntilDisplayed(timeTableUpdatePage.getSaveButton());
      await timeTableUpdatePage.save();
      await waitUntilHidden(timeTableUpdatePage.getSaveButton());
      expect(await timeTableUpdatePage.getSaveButton().isPresent()).to.be.false;
    }

    await createTimeTable();
    await timeTableComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeCreate = await timeTableComponentsPage.countDeleteButtons();
    await createTimeTable();

    await timeTableComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await timeTableComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last TimeTable', async () => {
    await timeTableComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await timeTableComponentsPage.countDeleteButtons();
    await timeTableComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    timeTableDeleteDialog = new TimeTableDeleteDialog();
    expect(await timeTableDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/beautyPointApp.timeTable.delete.question/);
    await timeTableDeleteDialog.clickOnConfirmButton();

    await timeTableComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await timeTableComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
