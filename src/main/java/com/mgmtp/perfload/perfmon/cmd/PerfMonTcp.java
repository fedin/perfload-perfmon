/*
 * Copyright (c) 2013 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mgmtp.perfload.perfmon.cmd;

import org.apache.commons.lang.text.StrBuilder;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.Tcp;

/**
 * @author rnaegele
 */
public class PerfMonTcp extends BasePerfMonCommand {

	private static final String TYPE_TCP = "tcp";

	private final boolean normalize;
	private long initialActiveOpens;
	private long initialPassiveOpens;
	private long initialAttemptFails;
	private long initialEstabResets;
	private long initialCurrEstab;
	private long initialInSegs;
	private long initialOutSegs;
	private long initialRetransSegs;
	private long initialInErrs;
	private long initialOutRsts;

	private boolean initialRun = true;
	private final String csvHeader = "aco<SEP>pco<SEP>fca<SEP>crr<SEP>ce<SEP>sr<SEP>sso<SEP>srt<SEP>bsr<SEP>rs".replace("<SEP>",
			separator);

	public PerfMonTcp(final String separator, final boolean csv, final boolean normalize) {
		super(separator, csv);
		this.normalize = normalize;
	}

	@Override
	public String getCsvHeader() {
		return csvHeader;
	}

	@Override
	public String executeCommand(final SigarProxy sigar) {
		StrBuilder sb = new StrBuilder(200);

		try {
			Tcp tcp = sigar.getTcp();

			long activeOpens = tcp.getActiveOpens();
			long passiveOpens = tcp.getPassiveOpens();
			long attemptFails = tcp.getAttemptFails();
			long estabResets = tcp.getEstabResets();
			long currEstab = tcp.getCurrEstab();
			long inSegs = tcp.getInSegs();
			long outSegs = tcp.getOutSegs();
			long retransSegs = tcp.getRetransSegs();
			long inErrs = tcp.getInErrs();
			long outRsts = tcp.getOutRsts();

			if (normalize) {
				if (initialRun) {
					initialActiveOpens = activeOpens;
					initialPassiveOpens = passiveOpens;
					initialAttemptFails = attemptFails;
					initialEstabResets = estabResets;
					initialCurrEstab = currEstab;
					initialInSegs = inSegs;
					initialOutSegs = outSegs;
					initialRetransSegs = retransSegs;
					initialInErrs = inErrs;
					initialOutRsts = outRsts;
					initialRun = false;
				}

				activeOpens = activeOpens - initialActiveOpens;
				passiveOpens = passiveOpens - initialPassiveOpens;
				attemptFails = attemptFails - initialAttemptFails;
				estabResets = estabResets - initialEstabResets;
				currEstab = currEstab - initialCurrEstab;
				inSegs = inSegs - initialInSegs;
				outSegs = outSegs - initialOutSegs;
				retransSegs = retransSegs - initialRetransSegs;
				inErrs = inErrs - initialInErrs;
				outRsts = outRsts - initialOutRsts;
			}

			if (!csv) {
				sb.append(TYPE_TCP);
				sb.append(separator);
			}

			sb.append(activeOpens); // active connections openings
			sb.append(separator);
			sb.append(passiveOpens); // passive connection openings
			sb.append(separator);
			sb.append(attemptFails); // failed connection attempts
			sb.append(separator);
			sb.append(estabResets); // connection resets received
			sb.append(separator);
			sb.append(currEstab);// connections established
			sb.append(separator);
			sb.append(inSegs); // segments received
			sb.append(separator);
			sb.append(outSegs); // segments send out
			sb.append(separator);
			sb.append(retransSegs);
			sb.append(separator); // segments retransmitted
			sb.append(inErrs); // bad segments received
			sb.append(separator);
			sb.append(outRsts); // resets sent
		} catch (SigarException ex) {
			log.error("Error reading TCP information: " + ex.getMessage(), ex);
		}

		return sb.toString();
	}
}
