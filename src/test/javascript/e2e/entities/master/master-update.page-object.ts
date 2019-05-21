import { element, by, ElementFinder } from 'protractor';

export default class MasterUpdatePage {
  pageTitle: ElementFinder = element(by.id('beautyPointApp.master.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  salonSelect: ElementFinder = element(by.css('select#master-salon'));
  categorySelect: ElementFinder = element(by.css('select#master-category'));
  userSelect: ElementFinder = element(by.css('select#master-user'));

  getPageTitle() {
    return this.pageTitle;
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
