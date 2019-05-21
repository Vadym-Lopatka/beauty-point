import { element, by, ElementFinder } from 'protractor';

export default class OfferUpdatePage {
  pageTitle: ElementFinder = element(by.id('beautyPointApp.offer.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  nameInput: ElementFinder = element(by.css('input#offer-name'));
  descriptionInput: ElementFinder = element(by.css('input#offer-description'));
  priceLowInput: ElementFinder = element(by.css('input#offer-priceLow'));
  priceHighInput: ElementFinder = element(by.css('input#offer-priceHigh'));
  activeInput: ElementFinder = element(by.css('input#offer-active'));
  statusSelect: ElementFinder = element(by.css('select#offer-status'));
  salonSelect: ElementFinder = element(by.css('select#offer-salon'));
  imageSelect: ElementFinder = element(by.css('select#offer-image'));
  categorySelect: ElementFinder = element(by.css('select#offer-category'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setNameInput(name) {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput() {
    return this.nameInput.getAttribute('value');
  }

  async setDescriptionInput(description) {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput() {
    return this.descriptionInput.getAttribute('value');
  }

  async setPriceLowInput(priceLow) {
    await this.priceLowInput.sendKeys(priceLow);
  }

  async getPriceLowInput() {
    return this.priceLowInput.getAttribute('value');
  }

  async setPriceHighInput(priceHigh) {
    await this.priceHighInput.sendKeys(priceHigh);
  }

  async getPriceHighInput() {
    return this.priceHighInput.getAttribute('value');
  }

  getActiveInput() {
    return this.activeInput;
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
