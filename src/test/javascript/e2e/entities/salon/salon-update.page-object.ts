import { element, by, ElementFinder } from 'protractor';

export default class SalonUpdatePage {
  pageTitle: ElementFinder = element(by.id('beautyPointApp.salon.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  nameInput: ElementFinder = element(by.css('input#salon-name'));
  sloganInput: ElementFinder = element(by.css('input#salon-slogan'));
  locationInput: ElementFinder = element(by.css('input#salon-location'));
  statusSelect: ElementFinder = element(by.css('select#salon-status'));
  systemCommentInput: ElementFinder = element(by.css('input#salon-systemComment'));
  typeSelect: ElementFinder = element(by.css('select#salon-type'));
  imageSelect: ElementFinder = element(by.css('select#salon-image'));
  timeTableSelect: ElementFinder = element(by.css('select#salon-timeTable'));
  categorySelect: ElementFinder = element(by.css('select#salon-category'));
  ownerSelect: ElementFinder = element(by.css('select#salon-owner'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setNameInput(name) {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput() {
    return this.nameInput.getAttribute('value');
  }

  async setSloganInput(slogan) {
    await this.sloganInput.sendKeys(slogan);
  }

  async getSloganInput() {
    return this.sloganInput.getAttribute('value');
  }

  async setLocationInput(location) {
    await this.locationInput.sendKeys(location);
  }

  async getLocationInput() {
    return this.locationInput.getAttribute('value');
  }

  async setStatusSelect(status) {
    await this.statusSelect.sendKeys(status);
  }

  async getStatusSelect() {
    return this.statusSelect.element(by.css('option:checked')).getText();
  }

  async statusSelectLastOption() {
    await this.statusSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }
  async setSystemCommentInput(systemComment) {
    await this.systemCommentInput.sendKeys(systemComment);
  }

  async getSystemCommentInput() {
    return this.systemCommentInput.getAttribute('value');
  }

  async setTypeSelect(type) {
    await this.typeSelect.sendKeys(type);
  }

  async getTypeSelect() {
    return this.typeSelect.element(by.css('option:checked')).getText();
  }

  async typeSelectLastOption() {
    await this.typeSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }
  async imageSelectLastOption() {
    await this.imageSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async imageSelectOption(option) {
    await this.imageSelect.sendKeys(option);
  }

  getImageSelect() {
    return this.imageSelect;
  }

  async getImageSelectedOption() {
    return this.imageSelect.element(by.css('option:checked')).getText();
  }

  async timeTableSelectLastOption() {
    await this.timeTableSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async timeTableSelectOption(option) {
    await this.timeTableSelect.sendKeys(option);
  }

  getTimeTableSelect() {
    return this.timeTableSelect;
  }

  async getTimeTableSelectedOption() {
    return this.timeTableSelect.element(by.css('option:checked')).getText();
  }

  async categorySelectLastOption() {
    await this.categorySelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async categorySelectOption(option) {
    await this.categorySelect.sendKeys(option);
  }

  getCategorySelect() {
    return this.categorySelect;
  }

  async getCategorySelectedOption() {
    return this.categorySelect.element(by.css('option:checked')).getText();
  }

  async ownerSelectLastOption() {
    await this.ownerSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async ownerSelectOption(option) {
    await this.ownerSelect.sendKeys(option);
  }

  getOwnerSelect() {
    return this.ownerSelect;
  }

  async getOwnerSelectedOption() {
    return this.ownerSelect.element(by.css('option:checked')).getText();
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
