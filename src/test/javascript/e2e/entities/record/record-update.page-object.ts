import { element, by, ElementFinder } from 'protractor';

export default class RecordUpdatePage {
  pageTitle: ElementFinder = element(by.id('beautyPointApp.record.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  bookingTimeInput: ElementFinder = element(by.css('input#record-bookingTime'));
  durationInput: ElementFinder = element(by.css('input#record-duration'));
  totalPriceInput: ElementFinder = element(by.css('input#record-totalPrice'));
  orderStatusSelect: ElementFinder = element(by.css('select#record-orderStatus'));
  commentInput: ElementFinder = element(by.css('input#record-comment'));
  masterSelect: ElementFinder = element(by.css('select#record-master'));
  variantSelect: ElementFinder = element(by.css('select#record-variant'));
  optionSelect: ElementFinder = element(by.css('select#record-option'));
  userSelect: ElementFinder = element(by.css('select#record-user'));
  salonSelect: ElementFinder = element(by.css('select#record-salon'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setBookingTimeInput(bookingTime) {
    await this.bookingTimeInput.sendKeys(bookingTime);
  }

  async getBookingTimeInput() {
    return this.bookingTimeInput.getAttribute('value');
  }

  async setDurationInput(duration) {
    await this.durationInput.sendKeys(duration);
  }

  async getDurationInput() {
    return this.durationInput.getAttribute('value');
  }

  async setTotalPriceInput(totalPrice) {
    await this.totalPriceInput.sendKeys(totalPrice);
  }

  async getTotalPriceInput() {
    return this.totalPriceInput.getAttribute('value');
  }

  async setOrderStatusSelect(orderStatus) {
    await this.orderStatusSelect.sendKeys(orderStatus);
  }

  async getOrderStatusSelect() {
    return this.orderStatusSelect.element(by.css('option:checked')).getText();
  }

  async orderStatusSelectLastOption() {
    await this.orderStatusSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }
  async setCommentInput(comment) {
    await this.commentInput.sendKeys(comment);
  }

  async getCommentInput() {
    return this.commentInput.getAttribute('value');
  }

  async masterSelectLastOption() {
    await this.masterSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async masterSelectOption(option) {
    await this.masterSelect.sendKeys(option);
  }

  getMasterSelect() {
    return this.masterSelect;
  }

  async getMasterSelectedOption() {
    return this.masterSelect.element(by.css('option:checked')).getText();
  }

  async variantSelectLastOption() {
    await this.variantSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async variantSelectOption(option) {
    await this.variantSelect.sendKeys(option);
  }

  getVariantSelect() {
    return this.variantSelect;
  }

  async getVariantSelectedOption() {
    return this.variantSelect.element(by.css('option:checked')).getText();
  }

  async optionSelectLastOption() {
    await this.optionSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async optionSelectOption(option) {
    await this.optionSelect.sendKeys(option);
  }

  getOptionSelect() {
    return this.optionSelect;
  }

  async getOptionSelectedOption() {
    return this.optionSelect.element(by.css('option:checked')).getText();
  }

  async userSelectLastOption() {
    await this.userSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async userSelectOption(option) {
    await this.userSelect.sendKeys(option);
  }

  getUserSelect() {
    return this.userSelect;
  }

  async getUserSelectedOption() {
    return this.userSelect.element(by.css('option:checked')).getText();
  }

  async salonSelectLastOption() {
    await this.salonSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async salonSelectOption(option) {
    await this.salonSelect.sendKeys(option);
  }

  getSalonSelect() {
    return this.salonSelect;
  }

  async getSalonSelectedOption() {
    return this.salonSelect.element(by.css('option:checked')).getText();
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
