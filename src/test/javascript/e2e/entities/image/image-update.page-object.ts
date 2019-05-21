import { element, by, ElementFinder } from 'protractor';

export default class ImageUpdatePage {
  pageTitle: ElementFinder = element(by.id('beautyPointApp.image.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  sourceInput: ElementFinder = element(by.css('input#image-source'));
  imageTypeSelect: ElementFinder = element(by.css('select#image-imageType'));
  ownerSelect: ElementFinder = element(by.css('select#image-owner'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setSourceInput(source) {
    await this.sourceInput.sendKeys(source);
  }

  async getSourceInput() {
    return this.sourceInput.getAttribute('value');
  }

  async setImageTypeSelect(imageType) {
    await this.imageTypeSelect.sendKeys(imageType);
  }

  async getImageTypeSelect() {
    return this.imageTypeSelect.element(by.css('option:checked')).getText();
  }

  async imageTypeSelectLastOption() {
    await this.imageTypeSelect
      .all(by.tagName('option'))
      .last()
      .click();
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
