// src/lib/dateRange.ts

export type DateRange = { from: string; to: string };

export function lastNDaysRange(days: number): DateRange {
    const to = new Date();
    const from = new Date();
    from.setDate(to.getDate() - days);

    const toStr = to.toISOString().slice(0, 10);
    const fromStr = from.toISOString().slice(0, 10);

    return { from: fromStr, to: toStr };
}
