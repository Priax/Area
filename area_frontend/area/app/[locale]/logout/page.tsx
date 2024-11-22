"use client"

import { deleteCookie, getCookie, getCookies } from 'cookies-next';
import {redirect} from '@/i18n/routing';

export default function loogut() {
    deleteCookie("clientId");
    deleteCookie("userToken");
    redirect('/');
}