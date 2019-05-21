/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import SubscriberComponentsPage from './subscriber.page-object';
import { SubscriberDeleteDialog } from './subscriber.page-object';
import SubscriberUpdatePage from './subscriber-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Subscriber e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let subscriberUpdatePage: SubscriberUpdatePage;
  let subscriberComponentsPage: SubscriberComponentsPage;
  let subscriberDeleteDialog: SubscriberDeleteDialog;

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

  it('should load Subscribers', async () => {
    await navBarPage.getEntityPage('subscriber');
    subscriberComponentsPage = new SubscriberComponentsPage();
    expect(await subscriberComponentsPage.getTitle().getText()).to.match(/Subscribers/);
  });

  it('should load create Subscriber page', async () => {
    await subscriberComponentsPage.clickOnCreateButton();
    subscriberUpdatePage = new SubscriberUpdatePage();
    expect(await subscriberUpdatePage.getPageTitle().getAttribute('id')).to.match(/beautyPointApp.subscriber.home.createOrEditLabel/);
    await subscriberUpdatePage.cancel();
  });

  it('should create and save Subscribers', async () => {
    async function createSubscriber() {
      await subscriberComponentsPage.clickOnCreateButton();
      await subscriberUpdatePage.setFirsNameInput('firsName');
      expect(await subscriberUpdatePage.getFirsNameInput()).to.match(/firsName/);
      await subscriberUpdatePage.setEmailInput('email');
      expect(await subscriberUpdatePage.getEmailInput()).to.match(/email/);
      await waitUntilDisplayed(subscriberUpdatePage.getSaveButton());
      await subscriberUpdatePage.save();
      await waitUntilHidden(subscriberUpdatePage.getSaveButton());
      expect(await subscriberUpdatePage.getSaveButton().isPresent()).to.be.false;
    }

    await createSubscriber();
    await subscriberComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeCreate = await subscriberComponentsPage.countDeleteButtons();
    await createSubscriber();

    await subscriberComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await subscriberComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Subscriber', async () => {
    await subscriberComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await subscriberComponentsPage.countDeleteButtons();
    await subscriberComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    subscriberDeleteDialog = new SubscriberDeleteDialog();
    expect(await subscriberDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/beautyPointApp.subscriber.delete.question/);
    await subscriberDeleteDialog.clickOnConfirmButton();

    await subscriberComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await subscriberComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
