import { element, by, ElementFinder } from 'protractor';

export default class VariantUpdatePage {
  pageTitle: ElementFinder = element(by.id('beautyPointApp.variant.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  nameInput: ElementFinder = element(by.css('input#variant-name'));
  priceInput: ElementFinder = element(by.css('input#variant-price'));
  sessionTimeInput: ElementFinder = element(by.css('input#variant-sessionTime'));
  activeInput: ElementFinder = element(by.css('input#variant-active'));
  offerSelect: ElementFinder = element(by.css('select#variant-offer'));
  executorSelect: ElementFinder = element(by.css('select#variant-executor'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setNameInput(name) {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput() {
    return this.nameInput.getAttribute('value');
  }

  async setPriceInput(price) {
    await this.priceInput.sendKeys(price);
  }

  async getPriceInput() {
    return this.priceInput.getAttribute('value');
  }

  async setSessionTimeInput(sessionTime) {
    await this.sessionTimeInput.sendKeys(sessionTime);
  }

  async getSessionTimeInput() {
    return this.sessionTimeInput.getAttribute('value');
  }

  getActiveInput() {
    return this.activeInput;
  }
  async offerSelectLastOption() {
    await this.offerSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async offerSelectOption(option) {
    await this.offerSelect.sendKeys(option);
  }

  getOfferSelect() {
    return this.offerSelect;
  }

  async getOfferSelectedOption() {
    return this.offerSelect.element(by.css('option:checked')).getText();
  }

  async executorSelectLastOption() {
    await this.executorSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async executorSelectOption(option) {
    await this.executorSelect.sendKeys(option);
  }

  getExecutorSelect() {
    return this.executorSelect;
  }

  async getExecutorSelectedOption() {
    return this.executorSelect.element(by.css('option:checked')).getText();
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
