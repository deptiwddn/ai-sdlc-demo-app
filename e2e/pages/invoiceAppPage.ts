import { expect, Page } from '@playwright/test';

export class InvoiceAppPage {
  constructor(private readonly page: Page) {}

  private formatMoney(amount: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);
  }

  async open() {
    await this.page.goto('/');
    await expect(this.page.getByRole('heading', { name: 'Invoice Approvals' })).toBeVisible();
  }

  async login(username: string, password: string) {
    await this.page.getByLabel('Username').fill(username);
    await this.page.getByLabel('Password').fill(password);
    await this.page.getByRole('button', { name: 'Sign in' }).click();
    await expect(this.page.getByRole('button', { name: 'Sign out' })).toBeVisible();
  }

  async createInvoice(customerName: string, amount: number) {
    const uniqueCustomerName = `${customerName} ${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
    await this.page.getByLabel('Customer name').fill(uniqueCustomerName);
    await this.page.getByLabel('Amount').fill(String(amount));
    await this.page.getByRole('button', { name: 'Create invoice' }).click();

    const formattedAmount = this.formatMoney(amount);
    const row = this.page.locator('#invoices-table tbody tr').filter({ hasText: uniqueCustomerName }).last();
    await expect(row).toContainText(uniqueCustomerName);
    await expect(row).toContainText(formattedAmount);

    return (await row.getByRole('cell').first().textContent()) ?? '';
  }

  async submitInvoice(invoiceId: string) {
    await this.page.getByRole('button', { name: `Submit invoice ${invoiceId} for approval` }).click();
    await expect(this.page.locator('#invoices-table tbody tr').filter({ hasText: invoiceId })).toContainText('Pending Approval');
  }

  async openApprovalsTab() {
    await this.page.getByRole('tab', { name: 'Approvals' }).click();
    await expect(this.page.getByRole('heading', { name: 'Pending approvals' })).toBeVisible();
  }

  async approveInvoice(invoiceId: string) {
    await this.page.getByRole('button', { name: `Approve invoice ${invoiceId}` }).click();
  }

  async rejectInvoice(invoiceId: string, reason: string) {
    await this.page.getByLabel(`Rejection reason for ${invoiceId}`).fill(reason);
    await this.page.getByRole('button', { name: `Reject invoice ${invoiceId}` }).click();
  }

  async expectDecisionError(message: string) {
    const error = this.page.locator('#decision-error');
    await expect(error).toContainText(message);
    await expect(error).toBeVisible();
  }

  async getInvoiceRow(invoiceId: string) {
    return this.page.locator('#invoices-table tbody tr').filter({ hasText: invoiceId });
  }

  async getInvoiceState(invoiceId: string) {
    const state = await this.page.evaluate(async ({ invoiceId }) => {
      const session = JSON.parse(sessionStorage.getItem('session') ?? 'null');
      const response = await fetch(`/api/invoices/${invoiceId}`, {
        headers: session ? { Authorization: `Bearer ${session.token}` } : undefined,
      });
      return response.ok ? (await response.json()).status : null;
    }, { invoiceId });

    return state;
  }

  async createInvoiceViaApi(customerName: string, amount: number) {
    return this.page.evaluate(async ({ customerName, amount }) => {
      const session = JSON.parse(sessionStorage.getItem('session') ?? 'null');
      const response = await fetch('/api/invoices', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(session ? { Authorization: `Bearer ${session.token}` } : {}),
        },
        body: JSON.stringify({ customerName, amount }),
      });
      const text = await response.text();
      const body = text ? JSON.parse(text) : null;
      return response.ok ? (body && typeof body.id === 'string' ? body.id : null) : null;
    }, { customerName, amount });
  }

  async submitInvoiceViaApi(invoiceId: string) {
    return this.page.evaluate(async ({ invoiceId }) => {
      const session = JSON.parse(sessionStorage.getItem('session') ?? 'null');
      const response = await fetch(`/api/invoices/${invoiceId}/submit`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(session ? { Authorization: `Bearer ${session.token}` } : {}),
        },
      });
      return response.ok;
    }, { invoiceId });
  }

  async approveInvoiceViaApi(invoiceId: string) {
    return this.page.evaluate(async ({ invoiceId }) => {
      const session = JSON.parse(sessionStorage.getItem('session') ?? 'null');
      const response = await fetch(`/api/approvals/${invoiceId}/approve`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(session ? { Authorization: `Bearer ${session.token}` } : {}),
        },
      });
      const text = await response.text();
      return { ok: response.ok, status: response.status, body: text || null };
    }, { invoiceId });
  }

  async rejectViaApi(invoiceId: string, reason: string) {
    return this.page.evaluate(async ({ invoiceId, reason }) => {
      const session = JSON.parse(sessionStorage.getItem('session') ?? 'null');
      const response = await fetch(`/api/approvals/${invoiceId}/reject`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(session ? { Authorization: `Bearer ${session.token}` } : {}),
        },
        body: JSON.stringify({ reason }),
      });
      const text = await response.text();
      return {
        ok: response.ok,
        status: response.status,
        body: text || null,
      };
    }, { invoiceId, reason });
  }
}
