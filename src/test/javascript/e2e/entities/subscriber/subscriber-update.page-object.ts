import { element, by, ElementFinder } from 'protractor';

export default class SubscriberUpdatePage {
  pageTitle: ElementFinder = element(by.id('beautyPointApp.subscriber.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  firsNameInput: ElementFinder = element(by.css('input#subscriber-firsName'));
  emailInput: ElementFinder = element(by.css('input#subscriber-email'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setFirsNameInput(firsName) {
    await this.firsNameInput.sendKeys(firsName);
  }

  async getFirsNameInput() {
    return this.firsNameInput.getAttribute('value');
  }

  async setEmailInput(email) {
    await this.emailInput.sendKeys(email);
  }

  async getEmailInput() {
    return this.emailInput.getAttribute('value');
  }

  async save() {
    await this.saveButton.click();
  }

  async cancel() {
    await this.cancelButton.click();
  }

  getSaveButton() {
    return this.saveButton;
  }
}
