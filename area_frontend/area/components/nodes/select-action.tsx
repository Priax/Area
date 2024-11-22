import React, { useState, useCallback, useEffect } from 'react';
import { getCookie } from 'cookies-next';
import { Handle, Position } from '@xyflow/react';
import '@/public/css/nodehandles.css';
import { Divider } from '@mui/material';

import { LogosDiscordIcon } from '../icons/discordLogo';
import { LogosGoogleGmail } from '../icons/gmailLogo';
import { SimpleIconsOsu } from '../icons/osuLogo';
import { LogosSpotifyIcon } from '../icons/spotifyLogo';
import { SimpleIconsRiotgames } from '../icons/riotLogo';
import { AkarIconsThreadsFill } from '../icons/threadsLogo';

import { useTranslations } from 'next-intl';

type SelectReactionProps = {
    data: {
        value: any;
        onChange: (id: string, newData: any) => void;
    };
    id: string;
};

type FormData = {
    service: string;
    action: string;
    actionTitle: string;
    field1: string;
    field2: string;
    field3: string;
    field4: string;
    nameList: string[];
    variable1: string;
    variable2: string;
    variable3: string;
    variable4: string;
    variable5: string;
};

export default function SelectReaction({ data, id }: SelectReactionProps) {
    const [formData, setFormData] = useState<FormData>({
        service: data.value?.service || '',
        action: data.value?.action || '',
        actionTitle: data.value?.actionTitle || '',
        field1: data.value?.field1 || '',
        field2: data.value?.field2 || '',
        field3: data.value?.field3 || '',
        field4: data.value?.field4 || '',
        nameList: data.value?.NameFields || [],
        variable1: data.value?.variable1 || '',
        variable2: data.value?.variable2 || '',
        variable3: data.value?.variable3 || '',
        variable4: data.value?.variable4 || '',
        variable5: data.value?.variable5 || '',
    });

    const t = useTranslations('SelectAction');

    const [services, setServices] = useState<string[]>([]);
    const [actions, setActions] = useState<string[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [nameFields, setNameFields] = useState<string[][]>([]);
    const [currentNameField, setCurrentNameField] = useState<string[]>([]);
    const [numberFields, setNumberFields] = useState<number[]>([]);
    const [numberVariables, setNumberVariables] = useState<number[]>([]);
    const [variables, setVariables] = useState<Record<string, string>[]>([]);
    const [numberCurrentField, setNumberCurrentField] = useState(0);
    const [numberCurrentVariables, setNumberCurrentVariables] = useState(0);
    const [myIndex, setMyIndex] = useState(0);
    const [currentActionTitle, setCurrentActionTitle] = useState('');

    type ServiceIconsType = {
        [key: string]: any;
    }

    const ServiceIcons : ServiceIconsType = {
        'DISCORD' : <LogosDiscordIcon/>,
        'GMAIL': <LogosGoogleGmail/>,
        'OSU': <SimpleIconsOsu color='#f062a1'/>,
        'SPOTIFY': <LogosSpotifyIcon/>,
        'RIOT': <SimpleIconsRiotgames/>,
        'THREADS': <AkarIconsThreadsFill/>
    }

    useEffect(() => {
        getServices();
    }, []);

    const handleChange = useCallback((evt: React.ChangeEvent<HTMLSelectElement | HTMLInputElement>) => {
        const { name, value } = evt.target;

        if (name === 'service' && value !== formData.service && value !== '') {
            setActions([]);
            getReactions(value);
        } else if (name === 'service' && value === '') {
            setActions([]);
            setNumberCurrentField(0);
            setNumberCurrentVariables(0);
            setMyIndex(0);
            setNumberVariables([]);
            setVariables([]);
        }

        if (name === 'action' && value !== '') {
            const actionIndex = actions.indexOf(value);
            if (numberFields && actionIndex !== -1 && numberFields[actionIndex] !== undefined) {
                const nActions = numberFields[actionIndex];
                const nVariables = numberVariables[actionIndex];
                setCurrentNameField(nameFields[actionIndex]);
                setNumberCurrentField(nActions);
                setNumberCurrentVariables(nVariables);
                setMyIndex(actionIndex);
                setFormData(prev => {
                    const newData = { ...prev, nameList: nameFields[actionIndex], action: value };
                    data.onChange(id, newData);
                    return newData;
                });
            } else {
                setNumberCurrentField(0);
            }
        }
        if (name === 'action' && value === '') {
            setNumberCurrentField(0);
        }

        setFormData(prev => {
            const newData = { ...prev, [name]: value };
            data.onChange(id, newData);
            return newData;
        });
    }, [id, data, formData, numberFields, actions, nameFields, variables, numberVariables]);

    const getServices = async () => {
        try {
            const response = await fetch('http://localhost:8080/services/', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'UserID': String(getCookie('clientId')),
                    'Authorization': String(getCookie('userToken')),
                },
            });

            if (!response.ok) {
                console.error(`Error: ${response.status} - ${response.statusText}`);
                return;
            }
            const data = await response.json();
            setServices(data);

        } catch (error) {
            console.error("Fetch error: ", error);
        }
    };

    const getReactions = async (service: string) => {
        try {
            const response = await fetch('http://localhost:8080/handlejson/service/actions', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'UserID': String(getCookie('clientId')),
                    'Service': service,
                    'Authorization': String(getCookie('userToken')),
                },
            });

            if (response.ok) {
                const data = await response.json();
                let list: string[] = [];
                let listVariables: Record<string, string>[] = [];
                let numVariables: number[] = [];
                let actionsLen: number[] = [];
                let nameList: string[][] = [];

                console.log(data);

                data.forEach((reactionData: any) => {
                    const parsedJson = JSON.parse(reactionData['value']);
                    list.push(parsedJson['Action']);
                    actionsLen.push(Object.keys(parsedJson).length - 1);
                    delete parsedJson[Object.keys(parsedJson)[0]];
                    nameList.push(Object.keys(parsedJson));

                    const parsedVariables = JSON.parse(reactionData['variables']);
                    numVariables.push(Object.keys(parsedVariables).length);
                    listVariables.push(parsedVariables);
                });

                setNumberFields(actionsLen);
                setNumberVariables(numVariables);
                setActions(list);
                setNameFields(nameList);
                setVariables(listVariables);
            } else {
                console.error(`Error: ${response.status} - ${response.statusText}`);
            }
        } catch (error) {
            console.error("Fetch error: ", error);
        }
    };

    return (
        <div className="p-3 bg-white rounded-lg shadow-md" style={{ minWidth: '250px', borderRadius: '19px' }}>
            <div className='text-center mb-6 font-bold text-2xl'>
                <input
                    name="actionTitle"
                    className='text-center nodrag w-full border border-gray-300 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 rounded-lg p-2 shadow-sm transition duration-200 ease-in-out'
                    placeholder={t('type')}
                    value={formData.actionTitle}
                    onChange={handleChange}
                />
            </div>
            {error && <p className="text-red-500">{error}</p>}
            <div className="space-y-4">
                <div>
                    <label className="block text-gray-700 font-medium mb-2">
                        {formData.service ? (
                            <div className="flex items-center gap-2">
                                {formData.service}
                                {ServiceIcons[formData.service]}
                            </div>
                        ) : (
                            t('service')
                        )}
                    </label>
                    <select
                        id="service-select"
                        name="service"
                        value={formData.service}
                        onChange={handleChange}
                        className="nodrag w-full border border-gray-300 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 rounded-lg p-2 text-gray-700 shadow-sm transition duration-200 ease-in-out"
                    >
                        <option value="">{t('noservice')}</option>
                        {services.map(service => (
                            <option key={service} value={service}>{service}</option>
                        ))}
                    </select>
                </div>
                <div>
                    <label htmlFor="action-select" className="block text-gray-700 font-medium mb-2">
                        {t('action')}
                    </label>
                    <select
                        id="action-select"
                        name="action"
                        value={formData.action}
                        onChange={handleChange}
                        className="nodrag w-full border border-gray-300 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 rounded-lg p-2 text-gray-700 shadow-sm transition duration-200 ease-in-out"
                    >
                        <option value="">{t('noaction')}</option>
                        {actions.map(action => (
                            <option key={action} value={action}>{action}</option>
                        ))}
                    </select>
                </div>
                <form>
                    {[...Array(numberCurrentField || 0)].map((_, index) => (
                        <div key={index}>
                            <label htmlFor={`field${index + 1}-input`} className="block text-gray-700 font-medium mb-2 mt-2">
                                {currentNameField[index]}
                            </label>
                            <input
                                id={`field${index + 1}-input`}
                                name={`field${index + 1}`}
                                type="text"
                                value={formData[`field${index + 1}` as keyof FormData]}
                                onChange={handleChange}
                                className="nodrag w-full border border-gray-300 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 rounded-lg p-2 text-gray-700 shadow-sm transition duration-200 ease-in-out"
                                placeholder={`Enter ${currentNameField[index]}`}
                            />
                        </div>
                    ))}
                </form>
                {formData.action && formData.service && (
                    <>
                        <Divider className='mt-3 mb-3'/>
                        <div className="text-center mt-3 mb-3 font-bold text-xl">
                            <h1>Variables</h1>
                        </div>
                        {variables[myIndex] && Object.keys(variables[myIndex]).map((key, index) => (
                            <div key={index}>
                                <label htmlFor={`variable${index + 1}-input`} className="block text-gray-700 font-medium mb-2 mt-2">
                                    {key}
                                </label>
                                <p>{String(variables[myIndex][key])}</p>
                            </div>
                        ))}
                    </>
                )}
            </div>
            <Handle type="source" position={Position.Right} />
        </div>
    );
}
