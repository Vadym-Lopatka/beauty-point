import { element, by, ElementFinder } from 'protractor';

export default class CategoryUpdatePage {
  pageTitle: ElementFinder = element(by.id('beautyPointApp.category.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  nameInput: ElementFinder = element(by.css('input#category-name'));
  mainInput: ElementFinder = element(by.css('input#category-main'));
  parentSelect: ElementFinder = element(by.css('select#category-parent'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setNameInput(name) {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput() {
    return this.nameInput.getAttribute('value');
  }

  getMainInput() {
    return this.mainInput;
  }
  async parentSelectLastOption() {
    await this.parentSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async parentSelectOption(option) {
    await this.parentSelect.sendKeys(option);
  }

  getParentSelect() {
    return this.parentSelect;
  }

  async getParentSelectedOption() {
    return this.parentSelect.element(by.css('option:checked')).getText();
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
