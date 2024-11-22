import {useLocale, useTranslations} from 'next-intl';
import LocaleSwitcherSelectAuth from './LocaleSwitcherSelectAuth';
import {routing} from '@/i18n/routing';

export default function LocaleSwitcherAuth() {
  const t = useTranslations('LocaleSwitcher');
  const locale = useLocale();

  return (
    <LocaleSwitcherSelectAuth defaultValue={locale} label={t('label')}>
      {routing.locales.map((cur) => (
        <option key={cur} value={cur}>
          {t('locale', {locale: cur})}
        </option>
      ))}
    </LocaleSwitcherSelectAuth>
  );
}