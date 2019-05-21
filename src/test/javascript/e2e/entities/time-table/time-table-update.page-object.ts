import { element, by, ElementFinder } from 'protractor';

export default class TimeTableUpdatePage {
  pageTitle: ElementFinder = element(by.id('beautyPointApp.timeTable.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  everyDayEqualInput: ElementFinder = element(by.css('input#time-table-everyDayEqual'));
  moInput: ElementFinder = element(by.css('input#time-table-mo'));
  tuInput: ElementFinder = element(by.css('input#time-table-tu'));
  weInput: ElementFinder = element(by.css('input#time-table-we'));
  thInput: ElementFinder = element(by.css('input#time-table-th'));
  frInput: ElementFinder = element(by.css('input#time-table-fr'));
  saInput: ElementFinder = element(by.css('input#time-table-sa'));
  suInput: ElementFinder = element(by.css('input#time-table-su'));

  getPageTitle() {
    return this.pageTitle;
  }

  getEveryDayEqualInput() {
    return this.everyDayEqualInput;
  }
  async setMoInput(mo) {
    await this.moInput.sendKeys(mo);
  }

  async getMoInput() {
    return this.moInput.getAttribute('value');
  }

  async setTuInput(tu) {
    await this.tuInput.sendKeys(tu);
  }

  async getTuInput() {
    return this.tuInput.getAttribute('value');
  }

  async setWeInput(we) {
    await this.weInput.sendKeys(we);
  }

  async getWeInput() {
    return this.weInput.getAttribute('value');
  }

  async setThInput(th) {
    await this.thInput.sendKeys(th);
  }

  async getThInput() {
    return this.thInput.getAttribute('value');
  }

  async setFrInput(fr) {
    await this.frInput.sendKeys(fr);
  }

  async getFrInput() {
    return this.frInput.getAttribute('value');
  }

  async setSaInput(sa) {
    await this.saInput.sendKeys(sa);
  }

  async getSaInput() {
    return this.saInput.getAttribute('value');
  }

  async setSuInput(su) {
    await this.suInput.sendKeys(su);
  }

  async getSuInput() {
    return this.suInput.getAttribute('value');
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
